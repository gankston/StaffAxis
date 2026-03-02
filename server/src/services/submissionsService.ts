import { db, query } from "../db/turso.js";
import type { DeviceAuth } from "./authService.js";
import { logger } from "../utils/logger.js";

export type CreateSubmissionResult =
  | { data: unknown; status: 200 | 201 }
  | { data: { code: "fk_missing"; missing: string[]; device_id: string; sector_id: string; employee_id: string }; status: 400 };

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

  if (missing.length > 0) {
    return {
      data: { code: "fk_missing", missing, device_id, sector_id, employee_id },
      status: 400,
    };
  }

  const now = Math.floor(Date.now() / 1000);
  await db.execute({
    sql: `INSERT INTO employees (id, sector_id, first_name, last_name, external_code, is_active, created_at, updated_at)
          VALUES (?, ?, 'Sin nombre', '', ?, 1, ?, ?) ON CONFLICT(id) DO NOTHING`,
    args: [employee_id, sector_id, employee_id, now, now],
  });
  logger.info("[StaffAxis] ensured employee exists", { employee_id });

  const existing = await db.execute({
    sql: `SELECT id FROM attendance_submissions WHERE device_id = ? AND employee_id = ? AND date = ? LIMIT 1`,
    args: [device_id, employee_id, date],
  });
  if (existing.rows.length > 0) {
    return { data: { ok: true, dedup: true }, status: 200 as const };
  }

  const id = crypto.randomUUID();
  try {
    await db.execute({
      sql: `INSERT INTO attendance_submissions
            (id, device_id, sector_id, encargado_name, employee_id, date, minutes_worked, check_in, check_out, notes, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?, ?)`,
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
    const row = await db.execute({
      sql: "SELECT * FROM attendance_submissions WHERE id = ?",
      args: [id],
    });
    return { data: row.rows[0], status: 201 as const };
  } catch (err) {
    const msg = err instanceof Error ? err.message : String(err);
    if (msg.includes("UNIQUE") || msg.includes("unique")) {
      const existing = await query(
        `SELECT * FROM attendance_submissions
         WHERE device_id = ? AND employee_id = ? AND date = ?
         ORDER BY updated_at DESC LIMIT 1`,
        [device_id, employee_id, date]
      );
      if (existing.rows.length > 0) {
        return { data: existing.rows[0], status: 200 as const };
      }
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
