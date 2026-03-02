import { db, transaction } from "../db/turso.js";
import type { AdminTokenPayload } from "../utils/security.js";
import { AppError } from "../middleware/errorHandler.js";

export async function getSubmissions(query: {
  status?: string;
  sector_id?: string;
  date?: string;
  limit: number;
  offset: number;
}) {
  const { status, sector_id: sectorId, date, limit, offset } = query;
  const conditions: string[] = [];
  const args: (string | number)[] = [];
  if (status) {
    conditions.push("status = ?");
    args.push(status);
  }
  if (sectorId && sectorId.length > 0) {
    conditions.push("sector_id = ?");
    args.push(sectorId);
  }
  if (date && date.length > 0) {
    conditions.push("date = ?");
    args.push(date);
  }
  const where = conditions.length > 0 ? `WHERE ${conditions.join(" AND ")}` : "";
  const result = await db.execute({
    sql: `SELECT * FROM attendance_submissions ${where} ORDER BY created_at DESC LIMIT ? OFFSET ?`,
    args: [...args, limit, offset],
  });
  return { submissions: result.rows };
}

export async function approveSubmission(
  admin: AdminTokenPayload,
  submissionId: string,
  body: {
    minutes_worked?: number | null;
    check_in?: string | null;
    check_out?: string | null;
    notes?: string | null;
  }
) {
  const subResult = await db.execute({
    sql: "SELECT * FROM attendance_submissions WHERE id = ?",
    args: [submissionId],
  });
  const submission = subResult.rows[0] as Record<string, unknown> | undefined;
  if (!submission) {
    throw new AppError(404, "Submission no encontrado", "not_found");
  }
  if ((submission.status as string) !== "pending") {
    throw new AppError(409, "Submission ya no está pendiente", "conflict");
  }
  const minutes_worked = (body.minutes_worked ?? submission.minutes_worked ?? null) as number | null;
  const check_in = (body.check_in ?? submission.check_in ?? null) as string | null;
  const check_out = (body.check_out ?? submission.check_out ?? null) as string | null;
  const notes = (body.notes ?? submission.notes ?? null) as string | null;
  const now = Math.floor(Date.now() / 1000);
  const employeeId = submission.employee_id as string;
  const sectorId = submission.sector_id as string;
  const date = submission.date as string;

  const { attendance, submission: subUpdated } = await transaction(async (tx) => {
    await tx.execute({
      sql: "UPDATE attendance_submissions SET status = 'approved', updated_at = ? WHERE id = ?",
      args: [now, submissionId],
    });
    const attendanceId = crypto.randomUUID();
    await tx.execute({
      sql: `INSERT INTO attendances
            (id, employee_id, sector_id, date, minutes_worked, check_in, check_out, notes,
             approved_from_submission_id, approved_by_admin_id, created_at, updated_at, deleted_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)
            ON CONFLICT(employee_id, date) DO UPDATE SET
              sector_id = excluded.sector_id,
              minutes_worked = excluded.minutes_worked,
              check_in = excluded.check_in,
              check_out = excluded.check_out,
              notes = excluded.notes,
              approved_from_submission_id = excluded.approved_from_submission_id,
              approved_by_admin_id = excluded.approved_by_admin_id,
              updated_at = excluded.updated_at,
              deleted_at = NULL`,
      args: [
        attendanceId,
        employeeId,
        sectorId,
        date,
        minutes_worked,
        check_in,
        check_out,
        notes,
        submissionId,
        admin.sub,
        now,
        now,
      ],
    });
    const attResult = await tx.execute({
      sql: "SELECT * FROM attendances WHERE employee_id = ? AND date = ?",
      args: [employeeId, date],
    });
    const subResult = await tx.execute({
      sql: "SELECT * FROM attendance_submissions WHERE id = ?",
      args: [submissionId],
    });
    return {
      attendance: (attResult.rows as Record<string, unknown>[])[0],
      submission: (subResult.rows as Record<string, unknown>[])[0],
    };
  });
  return { attendance, submission: subUpdated };
}

export async function getAttendances(query: {
  sector_id?: string;
  date?: string;
  employee_id?: string;
}) {
  const { sector_id: sectorId, date, employee_id: employeeId } = query;
  const conditions: string[] = ["deleted_at IS NULL"];
  const args: (string | number)[] = [];
  if (sectorId && sectorId.length > 0) {
    conditions.push("sector_id = ?");
    args.push(sectorId);
  }
  if (date && date.length > 0) {
    conditions.push("date = ?");
    args.push(date);
  }
  if (employeeId && employeeId.length > 0) {
    conditions.push("employee_id = ?");
    args.push(employeeId);
  }
  const where = `WHERE ${conditions.join(" AND ")}`;
  const result = await db.execute({
    sql: `SELECT * FROM attendances ${where} ORDER BY date DESC, updated_at DESC`,
    args,
  });
  return { attendances: result.rows };
}

export async function updateAttendance(
  id: string,
  body: {
    minutes_worked?: number | null;
    check_in?: string | null;
    check_out?: string | null;
    notes?: string | null;
  }
) {
  const existing = await db.execute({
    sql: "SELECT * FROM attendances WHERE id = ? AND deleted_at IS NULL",
    args: [id],
  });
  if (existing.rows.length === 0) {
    throw new AppError(404, "Attendance no encontrado", "not_found");
  }
  const row = existing.rows[0] as Record<string, unknown>;
  const minutes_worked = (body.minutes_worked ?? row.minutes_worked) as number | null;
  const check_in = (body.check_in ?? row.check_in) as string | null;
  const check_out = (body.check_out ?? row.check_out) as string | null;
  const notes = (body.notes ?? row.notes) as string | null;
  const now = Math.floor(Date.now() / 1000);
  await db.execute({
    sql: `UPDATE attendances SET minutes_worked = ?, check_in = ?, check_out = ?, notes = ?, updated_at = ? WHERE id = ?`,
    args: [minutes_worked, check_in, check_out, notes, now, id],
  });
  const updated = await db.execute({
    sql: "SELECT * FROM attendances WHERE id = ?",
    args: [id],
  });
  return updated.rows[0];
}

export async function deleteAttendance(id: string) {
  const existing = await db.execute({
    sql: "SELECT * FROM attendances WHERE id = ? AND deleted_at IS NULL",
    args: [id],
  });
  if (existing.rows.length === 0) {
    throw new AppError(404, "Attendance no encontrado", "not_found");
  }
  const now = Math.floor(Date.now() / 1000);
  await db.execute({
    sql: "UPDATE attendances SET deleted_at = ?, updated_at = ? WHERE id = ?",
    args: [now, now, id],
  });
  const updated = await db.execute({
    sql: "SELECT * FROM attendances WHERE id = ?",
    args: [id],
  });
  return updated.rows[0];
}

export async function upsertSector(body: { id: string; name: string }) {
  const { id, name } = body;
  const existing = await db.execute({ sql: "SELECT id FROM sectors WHERE id = ?", args: [id] });
  if (existing.rows.length > 0) {
    await db.execute({
      sql: "UPDATE sectors SET name = ? WHERE id = ?",
      args: [name, id],
    });
  } else {
    await db.execute({
      sql: "INSERT INTO sectors (id, name) VALUES (?, ?)",
      args: [id, name],
    });
  }
  const row = await db.execute({ sql: "SELECT * FROM sectors WHERE id = ?", args: [id] });
  return row.rows[0];
}

export async function upsertEmployee(body: {
  id: string;
  name: string;
  sector_id: string;
  external_code?: string | null;
}) {
  const { id, name, sector_id, external_code = null } = body;
  const parts = name.trim().split(/\s+/);
  const first_name = parts[0] ?? name;
  const last_name = parts.slice(1).join(" ") || " ";
  const now = Math.floor(Date.now() / 1000);
  const existing = await db.execute({ sql: "SELECT id FROM employees WHERE id = ?", args: [id] });
  if (existing.rows.length > 0) {
    await db.execute({
      sql: `UPDATE employees SET sector_id = ?, first_name = ?, last_name = ?, external_code = ?, updated_at = ? WHERE id = ?`,
      args: [sector_id, first_name, last_name, external_code, now, id],
    });
  } else {
    await db.execute({
      sql: `INSERT INTO employees (id, sector_id, first_name, last_name, external_code, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, 1, ?, ?)`,
      args: [id, sector_id, first_name, last_name, external_code, now, now],
    });
  }
  const row = await db.execute({ sql: "SELECT * FROM employees WHERE id = ?", args: [id] });
  return row.rows[0];
}
