import { db } from "../db/turso.js";
import type { DeviceAuth } from "./authService.js";

export async function getApproved(device: DeviceAuth, since: number) {
  const result = await db.execute({
    sql: `SELECT * FROM attendances
          WHERE sector_id = ? AND updated_at > ?
          ORDER BY updated_at ASC`,
    args: [device.sector_id, since],
  });
  return { attendances: result.rows };
}
