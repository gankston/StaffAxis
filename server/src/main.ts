import "dotenv/config";
import express from "express";
import { router } from "./routes/index.js";
import { healthRouter } from "./routes/health.js";
import { errorHandler } from "./middleware/errorHandler.js";
import { logger } from "./utils/logger.js";
import { bootstrapSchema, runSeedFile } from "./bootstrap/bootstrapSchema.js";
import { bootstrapAdmin } from "./services/bootstrap.js";
import * as path from "path";
import * as fs from "fs";

const PORT = process.env.PORT ?? 3000;

const app = express();

app.use(express.json());

// Logging básico
app.use((req, _res, next) => {
  logger.info(`${req.method} ${req.path}`);
  next();
});

app.use("/api", router);

// Health en raíz: /health y /health/db
app.use("/health", healthRouter);

// Endpoint /dev/seed solo en desarrollo (ejecuta db/seed.sql)
if (process.env.NODE_ENV === "development") {
  const seedPath = path.join(process.cwd(), "..", "db", "seed.sql");
  if (fs.existsSync(seedPath)) {
    app.post("/dev/seed", async (_req, res) => {
      try {
        const { executed } = await runSeedFile();
        res.json({ ok: true, executed });
      } catch (err) {
        res.status(500).json({ ok: false, error: String(err) });
      }
    });
  }
}

app.get("/", (_req, res) => {
  res.json({ name: "StaffAxis API", version: "1.0.0" });
});

app.use(errorHandler);

async function start() {
  await bootstrapSchema().catch((err) => {
    logger.error("Bootstrap schema falló:", err);
    throw err;
  });
  await bootstrapAdmin().catch(() => {});
  app.listen(PORT, () => {
    logger.info(`Servidor en http://localhost:${PORT}`);
  });
}

start();
