import { Router } from "express";
import { z } from "zod";
import { db, transaction } from "../db/turso.js";
import { requireAdminAuth } from "../middleware/auth.js";
import { validate, validateQuery, validateParams } from "../middleware/validate.js";
import { AppError } from "../middleware/errorHandler.js";

export const adminRouter = Router();

const uuidParam = z.object({ id: z.string().uuid() });
const dateString = z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "date formato YYYY-MM-DD");

const submissionsQuerySchema = z.object({
  status: z.enum(["pending", "approved", "rejected"]).optional(),
  sector_id: z.string().optional(),
  date: z.union([dateString, z.literal("")]).optional(),
  limit: z.coerce.number().int().min(1).max(100).optional().default(20),
  offset: z.coerce.number().int().min(0).optional().default(0),
});

const attendancesQuerySchema = z.object({
  sector_id: z.string().optional(),
  date: z.union([dateString, z.literal("")]).optional(),
  employee_id: z.string().optional(),
});

const approveBodySchema = z.object({
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});

const updateAttendanceSchema = z.object({
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});

type SubmissionsQuery = z.infer<typeof submissionsQuerySchema>;

/** GET /admin/submissions — lista paginada, filtros: status, sector_id, date */
adminRouter.get(
  "/submissions",
  requireAdminAuth,
  validateQuery(submissionsQuerySchema),
  async (req, res) => {
    const { status, sector_id: sectorId, date, limit, offset } = req.validatedQuery as SubmissionsQuery;

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
    sql: `
      SELECT * FROM attendance_submissions
      ${where}
      ORDER BY created_at DESC
      LIMIT ? OFFSET ?
    `,
    args: [...args, limit, offset],
  });

  res.json({ submissions: result.rows });
});

/** POST /admin/submissions/:id/approve — aprobar submission y upsert attendances */
adminRouter.post(
  "/submissions/:id/approve",
  requireAdminAuth,
  validateParams(uuidParam),
  validate(approveBodySchema),
  async (req, res) => {
    const admin = req.adminUser!;
    const submissionId = req.params.id;
    const body = req.body as z.infer<typeof approveBodySchema>;

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
        sql: `
          UPDATE attendance_submissions SET status = 'approved', updated_at = ? WHERE id = ?
        `,
        args: [now, submissionId],
      });

      const attendanceId = crypto.randomUUID();
      await tx.execute({
        sql: `
          INSERT INTO attendances
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
            deleted_at = NULL
        `,
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
      const attRows = attResult.rows as Record<string, unknown>[];
      const subResult = await tx.execute({
        sql: "SELECT * FROM attendance_submissions WHERE id = ?",
        args: [submissionId],
      });
      const subRows = subResult.rows as Record<string, unknown>[];
      return {
        attendance: attRows[0],
        submission: subRows[0],
      };
    });

    res.json({ attendance, submission: subUpdated });
  }
);

type AttendancesQuery = z.infer<typeof attendancesQuerySchema>;

/** GET /admin/attendances — listado con filtros sector_id, date, employee_id */
adminRouter.get(
  "/attendances",
  requireAdminAuth,
  validateQuery(attendancesQuerySchema),
  async (req, res) => {
    const { sector_id: sectorId, date, employee_id: employeeId } = req.validatedQuery as AttendancesQuery;

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
    sql: `
      SELECT * FROM attendances
      ${where}
      ORDER BY date DESC, updated_at DESC
    `,
    args,
  });

  res.json({ attendances: result.rows });
  }
);

/** PUT /admin/attendances/:id — actualizar campos y updated_at */
adminRouter.put(
  "/attendances/:id",
  requireAdminAuth,
  validateParams(uuidParam),
  validate(updateAttendanceSchema),
  async (req, res) => {
    const id = req.params.id;
    const body = req.body as z.infer<typeof updateAttendanceSchema>;

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
      sql: `
        UPDATE attendances SET
          minutes_worked = ?, check_in = ?, check_out = ?, notes = ?, updated_at = ?
        WHERE id = ?
      `,
      args: [minutes_worked, check_in, check_out, notes, now, id],
    });

    const updated = await db.execute({
      sql: "SELECT * FROM attendances WHERE id = ?",
      args: [id],
    });
    res.json(updated.rows[0]);
  }
);

/** DELETE /admin/attendances/:id — soft delete */
adminRouter.delete(
  "/attendances/:id",
  requireAdminAuth,
  validateParams(uuidParam),
  async (req, res) => {
  const id = req.params.id;

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
  res.json(updated.rows[0]);
  }
);
