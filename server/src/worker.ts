/**
 * Cloudflare Workers entrypoint.
 * Init una sola vez por isolate (ensureInit).
 */

import { workerApp } from "./workerApp.js";
import { ensureInit } from "./init.js";

export default {
  async fetch(
    request: Request,
    env: Record<string, unknown>,
    ctx: unknown
  ): Promise<Response> {
    await ensureInit(env);
    return workerApp.fetch(request, env, ctx);
  },
};
