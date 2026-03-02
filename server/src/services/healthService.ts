import { query } from "../db/turso.js";
import { logger } from "../utils/logger.js";

export async function checkDb(): Promise<{ status: string; database: string; ping: unknown }> {
  try {
    const result = await query("SELECT 1 as ok");
    return {
      status: "ok",
      database: "connected",
      ping: result.rows[0] ?? { ok: 1 },
    };
  } catch (err) {
    logger.error("Health check DB failed:", err);
    throw Object.assign(new Error("DB disconnected"), { status: 503 });
  }
}
