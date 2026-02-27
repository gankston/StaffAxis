import { Router } from "express";
import { z } from "zod";
import { db } from "../db/turso.js";
import { requireDeviceAuth } from "../middleware/auth.js";
import { validateQuery } from "../middleware/validate.js";

export const approvedRouter = Router();

const approvedQuerySchema = z.object({
  since: z.coerce.number().int().min(0).optional().default(0),
});

type ApprovedQuery = z.infer<typeof approvedQuerySchema>;

/** GET /approved?since=timestamp — attendances del sector, updated_at > since, incluye deleted */
approvedRouter.get(
  "/",
  requireDeviceAuth,
  validateQuery(approvedQuerySchema),
  async (req, res) => {
    const device = req.deviceAuth!;
    const { since: sinceValid } = req.validatedQuery as ApprovedQuery;

    const result = await db.execute({
      sql: `
        SELECT * FROM attendances
        WHERE sector_id = ? AND updated_at > ?
        ORDER BY updated_at ASC
      `,
      args: [device.sector_id, sinceValid],
    });

    res.json({ attendances: result.rows });
  }
);
