import { Router } from "express";
import { db } from "../db/client.js";
import { logger } from "../utils/logger.js";

export const healthRouter = Router();

healthRouter.get("/", async (_req, res) => {
  try {
    await db.execute("SELECT 1");
    res.json({ status: "ok", database: "connected" });
  } catch (err) {
    logger.error("Health check DB failed:", err);
    res.status(503).json({ status: "error", database: "disconnected" });
  }
});
