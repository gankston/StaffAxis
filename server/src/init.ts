/**
 * Init una sola vez por isolate. Evita bootstrap repetido que consume CPU.
 * NUNCA re-ejecutar schema/bootstrap por request.
 */
import { bootstrapSchema } from "./bootstrap/bootstrapSchema.js";
import { bootstrapAdmin } from "./services/bootstrap.js";
import { logger } from "./utils/logger.js";

let INIT_PROMISE: Promise<void> | null = null;

export async function ensureInit(env: Record<string, unknown>): Promise<void> {
  if (INIT_PROMISE) return INIT_PROMISE;
  INIT_PROMISE = (async () => {
    for (const [k, v] of Object.entries(env)) {
      if (typeof v === "string") {
        (process.env as Record<string, string>)[k] = v;
      }
    }
    try {
      await bootstrapSchema();
      await bootstrapAdmin();
    } catch (err) {
      logger.error("Bootstrap failed:", err);
      INIT_PROMISE = null;
      throw err;
    }
  })();
  return INIT_PROMISE;
}
