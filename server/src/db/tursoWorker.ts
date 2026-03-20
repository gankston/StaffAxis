/**
 * Cliente Turso para Cloudflare Workers.
 * Usa @libsql/client/web (no node) para compatibilidad con Workers runtime.
 */
import { createClient } from "@libsql/client/web";
import type { Client } from "@libsql/client/web";
import { logger } from "../utils/logger.js";

export type TursoEnv = {
  TURSO_DATABASE_URL?: string;
  TURSO_AUTH_TOKEN?: string;
};

export function createTursoClient(env: TursoEnv): Client {
  const url = env.TURSO_DATABASE_URL?.trim();
  const authToken = env.TURSO_AUTH_TOKEN?.trim();
  if (!url || !authToken) {
    throw new Error("TURSO_DATABASE_URL y TURSO_AUTH_TOKEN son obligatorios");
  }
  return createClient({ url, authToken });
}

/**
 * Encola un submission en outbox_submissions para reintento posterior.
 * Usa INSERT OR IGNORE cuando dedup_key está definido (evita duplicados).
 */
export async function enqueueSubmission(
  client: Client,
  payloadJson: string,
  dedupKey?: string
): Promise<{ id: string }> {
  const id = crypto.randomUUID();
  const now = Math.floor(Date.now() / 1000);

  await client.execute({
    sql: `INSERT OR IGNORE INTO outbox_submissions
          (id, created_at, status, attempts, next_retry_at, last_error, dedup_key, payload_json)
          VALUES (?, ?, 'pending', 0, ?, NULL, ?, ?)`,
    args: [id, now, now, dedupKey ?? null, payloadJson],
  });

  logger.info("OUTBOX_ENQUEUE", { id, dedupKey });
  return { id };
}
