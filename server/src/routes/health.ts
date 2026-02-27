import { Router } from "express";
import { query } from "../db/turso.js";
import { logger } from "../utils/logger.js";

export const healthRouter = Router();

healthRouter.get("/", (_req, res) => {
  res.json({ status: "ok" });
});

healthRouter.get("/db", async (_req, res) => {
  try {
    const result = await query("SELECT 1 as ok");
    res.json({
      status: "ok",
      database: "connected",
      ping: result.rows[0] ?? { ok: 1 },
    });
  } catch (err) {
    logger.error("Health check DB failed:", err);
    res.status(503).json({ status: "error", database: "disconnected" });
  }
});
