import { Router } from "express";
import { db } from "../db/turso.js";
import { requireDeviceAuth } from "../middleware/auth.js";

export const approvedRouter = Router();

/** GET /approved?since=timestamp — attendances del sector, updated_at > since, incluye deleted */
approvedRouter.get("/", requireDeviceAuth, async (req, res) => {
  const device = req.deviceAuth!;
  const since = req.query.since;
  const sinceNum = since ? Number(since) : 0;
  const sinceValid = Number.isInteger(sinceNum) && sinceNum >= 0 ? sinceNum : 0;

  const result = await db.execute({
    sql: `
      SELECT * FROM attendances
      WHERE sector_id = ? AND updated_at > ?
      ORDER BY updated_at ASC
    `,
    args: [device.sector_id, sinceValid],
  });

  res.json({ attendances: result.rows });
});
