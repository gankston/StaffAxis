import { Router } from "express";
import { z } from "zod";
import { db } from "../db/turso.js";
import { requireDeviceAuth } from "../middleware/auth.js";
import { validate } from "../middleware/validate.js";
import { AppError } from "../middleware/errorHandler.js";

export const submissionsRouter = Router();

const createSubmissionSchema = z.object({
  employee_id: z.string().min(1),
  date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "YYYY-MM-DD"),
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});

/** POST /submissions — crear submission (idempotente) */
submissionsRouter.post(
  "/",
  requireDeviceAuth,
  validate(createSubmissionSchema),
  async (req, res) => {
    const device = req.deviceAuth!;
    const body = req.body as z.infer<typeof createSubmissionSchema>;
    const {
      employee_id,
      date,
      minutes_worked = null,
      check_in = null,
      check_out = null,
      notes = null,
    } = body;

    const id = crypto.randomUUID();
    const now = Math.floor(Date.now() / 1000);

    try {
      await db.execute({
        sql: `
          INSERT INTO attendance_submissions
          (id, device_id, sector_id, encargado_name, employee_id, date, minutes_worked, check_in, check_out, notes, status, created_at, updated_at)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', ?, ?)
        `,
        args: [
          id,
          device.device_id,
          device.sector_id,
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
      res.status(201).json(row.rows[0]);
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      if (msg.includes("UNIQUE") || msg.includes("unique")) {
        const existing = await db.execute({
          sql: `
            SELECT * FROM attendance_submissions
            WHERE device_id = ? AND employee_id = ? AND date = ?
            ORDER BY updated_at DESC LIMIT 1
          `,
          args: [device.device_id, employee_id, date],
        });
        if (existing.rows.length > 0) {
          return res.status(200).json(existing.rows[0]);
        }
      }
      throw err;
    }
  }
);

/** GET /submissions/mine?since=timestamp — submissions del device desde since */
submissionsRouter.get("/mine", requireDeviceAuth, async (req, res) => {
  const device = req.deviceAuth!;
  const since = req.query.since;
  const sinceNum = since ? Number(since) : 0;
  const sinceValid = Number.isInteger(sinceNum) && sinceNum > 0 ? sinceNum : 0;

  const result = await db.execute({
    sql: `
      SELECT * FROM attendance_submissions
      WHERE device_id = ? AND updated_at >= ?
      ORDER BY updated_at ASC
    `,
    args: [device.device_id, sinceValid],
  });

  res.json({ submissions: result.rows });
});
