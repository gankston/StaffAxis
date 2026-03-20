import { db } from "../db/turso.js";
import type { DeviceAuth } from "./authService.js";

const DEFAULT_LIMIT = 200;

export async function getApproved(device: DeviceAuth, since: number, limit: number = DEFAULT_LIMIT) {
  const safeLimit = Math.min(Math.max(1, limit), 200);
  const result = await db.execute({
    sql: `SELECT * FROM attendances
          WHERE sector_id = ? AND updated_at > ?
          ORDER BY updated_at ASC
          LIMIT ?`,
    args: [device.sector_id, since, safeLimit],
  });
  return { attendances: result.rows };
}
