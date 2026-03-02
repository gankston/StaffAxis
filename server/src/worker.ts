/**
 * Cloudflare Workers entrypoint.
 * Usa Hono (fetch-native), sin Express.
 * Las variables de entorno (env) se inyectan en process.env antes de cada request.
 */

import { workerApp } from "./workerApp.js";
import { bootstrapSchema } from "./bootstrap/bootstrapSchema.js";
import { bootstrapAdmin } from "./services/bootstrap.js";
import { logger } from "./utils/logger.js";

// Bootstrap: schema y admin UNA SOLA VEZ por isolate (flag global). No en cada request.
let bootstrapDone = false;
let bootstrapPromise: Promise<void> | null = null;

async function ensureBootstrap(): Promise<void> {
  if (bootstrapDone) return;
  if (bootstrapPromise) return bootstrapPromise;
  bootstrapPromise = (async () => {
    try {
      await bootstrapSchema();
      await bootstrapAdmin();
      bootstrapDone = true;
    } catch (err) {
      logger.error("Bootstrap failed:", err);
      bootstrapPromise = null;
      throw err;
    }
  })();
  return bootstrapPromise;
}

export default {
  async fetch(
    request: Request,
    env: Record<string, unknown>,
    ctx: unknown
  ): Promise<Response> {
    for (const [k, v] of Object.entries(env)) {
      if (typeof v === "string") {
        (process.env as Record<string, string>)[k] = v;
      }
    }

    await ensureBootstrap();

    return workerApp.fetch(request, env, ctx);
  },
};
