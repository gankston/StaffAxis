import { db } from "../db/turso.js";
import * as submissionsService from "./submissionsService.js";
import type { DeviceAuth } from "./authService.js";
import { logger } from "../utils/logger.js";

const DRAIN_LIMIT = 50;
const MAX_BACKOFF_SEC = 60 * 60; // 60 min

type DrainResult = { processed: number; sent: number; retried: number; failed: number };

export async function drainOutbox(): Promise<DrainResult> {
  const now = Math.floor(Date.now() / 1000);
  const result = await db.execute({
    sql: `SELECT id, payload_json, attempts FROM outbox_submissions
          WHERE status = 'pending' AND next_retry_at <= ?
          ORDER BY created_at ASC
          LIMIT ?`,
    args: [now, DRAIN_LIMIT],
  });

  const rows = result.rows as unknown as Array<{ id: string; payload_json: string; attempts: number }>;
  const stats: DrainResult = { processed: 0, sent: 0, retried: 0, failed: 0 };

  for (const row of rows) {
    stats.processed++;
    try {
      const payload = JSON.parse(row.payload_json) as {
        body?: { employee_id: string; date: string; minutes_worked?: number | null; check_in?: string | null; check_out?: string | null; notes?: string | null };
        device_id?: string;
        sector_id?: string;
        encargado_name?: string;
      };

      const body = payload?.body;
      const deviceId = payload?.device_id;
      const sectorId = payload?.sector_id;
      const encargadoName = payload?.encargado_name;

      if (!body || !deviceId || !sectorId || !encargadoName) {
        await markFailed(row.id, "payload missing body or device context");
        stats.failed++;
        continue;
      }

      const devRow = await db.execute({
        sql: "SELECT id FROM devices WHERE device_id = ?",
        args: [deviceId],
      });
      const deviceDbId = (devRow.rows[0] as unknown as { id: string } | undefined)?.id;
      if (!deviceDbId) {
        await markFailed(row.id, "device not found");
        stats.failed++;
        continue;
      }

      const device: DeviceAuth = {
        device_id: deviceId,
        sector_id: sectorId,
        encargado_name: encargadoName,
        device_db_id: deviceDbId,
      };

      const subResult = await submissionsService.createSubmission(device, body);

      if (subResult.status === 200 || subResult.status === 201) {
        await db.execute({
          sql: "UPDATE outbox_submissions SET status = 'sent', last_error = NULL WHERE id = ?",
          args: [row.id],
        });
        stats.sent++;
      } else if (subResult.status === 400) {
        await markFailed(row.id, JSON.stringify(subResult.data));
        stats.failed++;
      } else {
        throw new Error(`Unexpected status ${subResult.status}`);
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      const isRetryable = msg.includes("1102") || msg.includes("503") || /5\d{2}/.test(msg);

      if (isRetryable) {
        const newAttempts = row.attempts + 1;
        const backoffSec = Math.min(MAX_BACKOFF_SEC, Math.pow(2, newAttempts));
        const nextRetry = now + backoffSec;

        await db.execute({
          sql: `UPDATE outbox_submissions SET attempts = ?, last_error = ?, next_retry_at = ? WHERE id = ?`,
          args: [newAttempts, msg.slice(0, 500), nextRetry, row.id],
        });
        stats.retried++;
      } else {
        await markFailed(row.id, msg);
        stats.failed++;
      }
    }
  }

  if (stats.processed > 0) {
    logger.info("OUTBOX_DRAIN", stats);
  }
  return stats;
}

async function markFailed(id: string, error: string): Promise<void> {
  await db.execute({
    sql: "UPDATE outbox_submissions SET status = 'failed', last_error = ? WHERE id = ?",
    args: [error.slice(0, 500), id],
  });
}
