import { db } from "../db/turso.js";
import type { DeviceAuth } from "./authService.js";
import { logger } from "../utils/logger.js";
import { logTiming } from "../utils/logger.js";

export type CreateSubmissionResult =
  | { data: unknown; status: 200 | 201 }
  | { data: { code: "fk_missing"; missing: string[]; device_id: string; sector_id: string; employee_id: string }; status: 400 }
  | { data: { code: "employee_not_found"; employee_id: string }; status: 400 };

export async function createSubmission(
  device: DeviceAuth,
  body: {
    employee_id: string;
    date: string;
    minutes_worked?: number | null;
    check_in?: string | null;
    check_out?: string | null;
    notes?: string | null;
  }
): Promise<CreateSubmissionResult> {
  const {
    employee_id,
    date,
    minutes_worked = null,
    check_in = null,
    check_out = null,
    notes = null,
  } = body;
  const { device_id, sector_id } = device;

  logger.info("POST /submissions", { device_id, sector_id, employee_id, date });

  const missing: string[] = [];
  const devCheck = await db.execute({ sql: "SELECT 1 FROM devices WHERE device_id = ?", args: [device_id] });
  if (devCheck.rows.length === 0) missing.push("devices");
  const secCheck = await db.execute({ sql: "SELECT 1 FROM sectors WHERE id = ?", args: [sector_id] });
  if (secCheck.rows.length === 0) missing.push("sectors");
  const empCheck = await db.execute({ sql: "SELECT 1 FROM employees WHERE id = ?", args: [employee_id] });
  if (empCheck.rows.length === 0) missing.push("employees");

  if (missing.length > 0) {
    if (missing.includes("employees")) {
      return {
        data: { code: "employee_not_found", employee_id },
        status: 400,
      };
    }
    return {
      data: { code: "fk_missing", missing, device_id, sector_id, employee_id },
      status: 400,
    };
  }

  const now = Math.floor(Date.now() / 1000);

  const id = crypto.randomUUID();
  try {
    const insertResult = await db.execute({
      sql: `INSERT INTO attendance_submissions
            (id, device_id, sector_id, encargado_name, employee_id, date, minutes_worked, check_in, check_out, notes, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?, ?)
            ON CONFLICT(device_id, employee_id, date) DO NOTHING`,
      args: [
        id,
        device_id,
        sector_id,
        device.encargado_name,
        employee_id,
        date,
        minutes_worked,
        check_in,
        check_out,
        notes,
        now,
        now,
      ],
    });

    const rowsAffected = insertResult.rowsAffected ?? -1;
    if (rowsAffected === 0) {
      logger.info("[StaffAxis] submissions dedup");
      return { data: { ok: true, dedup: true }, status: 200 as const };
    }

    if (rowsAffected >= 1) {
      const row = await db.execute({
        sql: "SELECT * FROM attendance_submissions WHERE id = ?",
        args: [id],
      });
      return { data: { ok: true, ...row.rows[0] }, status: 201 as const };
    }

    const existing = await db.execute({
      sql: "SELECT * FROM attendance_submissions WHERE device_id = ? AND employee_id = ? AND date = ?",
      args: [device_id, employee_id, date],
    });
    if (existing.rows.length > 0) {
      const row = existing.rows[0] as unknown as { id: string };
      if (row?.id === id) {
        return { data: { ok: true, ...existing.rows[0] }, status: 201 as const };
      }
      logger.info("[StaffAxis] submissions dedup");
      return { data: { ok: true, dedup: true }, status: 200 as const };
    }

    const row = await db.execute({
      sql: "SELECT * FROM attendance_submissions WHERE id = ?",
      args: [id],
    });
    return { data: { ok: true, ...row.rows[0] }, status: 201 as const };
  } catch (err) {
    const msg = err instanceof Error ? err.message : String(err);
    const isDedupConstraint =
      (msg.includes("SQLITE_CONSTRAINT") && msg.includes("ux_attendance_submissions_dedup")) ||
      msg.includes("UNIQUE constraint failed: attendance_submissions.device_id, attendance_submissions.employee_id, attendance_submissions.date");
    if (isDedupConstraint) {
      logger.info("[StaffAxis] submissions dedup");
      return { data: { ok: true, dedup: true }, status: 200 as const };
    }
    throw err;
  }
}

/**
 * Fast path: solo INSERT en attendance_submissions.
 * No se crean empleados desde el celular; si employee_id no existe se devuelve error 400.
 */
export async function createSubmissionFast(
  device: DeviceAuth,
  body: {
    employee_id: string;
    date: string;
    minutes_worked?: number | null;
    check_in?: string | null;
    check_out?: string | null;
    notes?: string | null;
  },
  t0: number
): Promise<{ ok: true; dedup?: boolean } | { ok: false; code: string; employee_id: string }> {
  const {
    employee_id,
    date,
    minutes_worked = null,
    check_in = null,
    check_out = null,
    notes = null,
  } = body;
  const { device_id, sector_id, encargado_name } = device;
  const now = Math.floor(Date.now() / 1000);

  const empCheck = await db.execute({ sql: "SELECT 1 FROM employees WHERE id = ?", args: [employee_id] });
  if (empCheck.rows.length === 0) {
    logTiming("/api/submissions", "checkEmployee", t0);
    logger.error(
      "checkEmployee: empleado no existe",
      `employee_id=${employee_id} sector_id=${sector_id}`
    );
    return { ok: false, code: "employee_not_found", employee_id };
  }
  logTiming("/api/submissions", "checkEmployee", t0);

  const id = crypto.randomUUID();
  try {
    const insertResult = await db.execute({
      sql: `INSERT INTO attendance_submissions
            (id, device_id, sector_id, encargado_name, employee_id, date, minutes_worked, check_in, check_out, notes, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?, ?)
            ON CONFLICT(device_id, employee_id, date, check_in, check_out, minutes_worked) DO NOTHING`,
      args: [
        id,
        device_id,
        sector_id,
        encargado_name,
        employee_id,
        date,
        minutes_worked,
        check_in,
        check_out,
        notes,
        now,
        now,
      ],
    });

    const rowsAffected = insertResult.rowsAffected ?? 0;
    logTiming("/api/submissions", "upsertSubmission", t0);
    return { ok: true, dedup: rowsAffected === 0 };
  } catch (err) {
    const msg = err instanceof Error ? err.message : String(err);
    const isDedup =
      msg.includes("SQLITE_CONSTRAINT") ||
      msg.includes("UNIQUE constraint failed");
    if (isDedup) {
      logTiming("/api/submissions", "upsertSubmission", t0);
      return { ok: true, dedup: true };
    }
    throw err;
  }
}

export async function getMine(device: DeviceAuth, since: number) {
  const result = await db.execute({
    sql: `SELECT * FROM attendance_submissions
          WHERE device_id = ? AND updated_at >= ?
          ORDER BY updated_at ASC`,
    args: [device.device_id, since],
  });
  return { submissions: result.rows };
}
