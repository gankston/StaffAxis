import { Router } from "express";
import { z } from "zod";
import { db, transaction } from "../db/turso.js";
import { requireAdminAuth } from "../middleware/auth.js";
import { validate } from "../middleware/validate.js";
import { AppError } from "../middleware/errorHandler.js";

export const adminRouter = Router();

const approveBodySchema = z.object({
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});

/** GET /admin/submissions — lista paginada, filtros: status, sector_id, date */
adminRouter.get("/submissions", requireAdminAuth, async (req, res) => {
  const status = (req.query.status as string) ?? "";
  const sectorId = (req.query.sector_id as string) ?? "";
  const date = (req.query.date as string) ?? "";
  const limit = Math.min(Math.max(1, Number(req.query.limit) || 20), 100);
  const offset = Math.max(0, Number(req.query.offset) || 0);

  const conditions: string[] = [];
  const args: (string | number)[] = [];

  if (status) {
    conditions.push("status = ?");
    args.push(status);
  }
  if (sectorId) {
    conditions.push("sector_id = ?");
    args.push(sectorId);
  }
  if (date) {
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
      throw new AppError(404, "Submission no encontrado");
    }
    if ((submission.status as string) !== "pending") {
      throw new AppError(400, "Submission ya no está pendiente");
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
