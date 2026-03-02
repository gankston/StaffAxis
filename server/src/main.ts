import "dotenv/config";
import * as fs from "fs";
import * as path from "path";
import { createApp } from "./app.js";
import { logger } from "./utils/logger.js";
import { bootstrapSchema } from "./bootstrap/bootstrapSchema.js";
import { bootstrapAdmin } from "./services/bootstrap.js";
import { runSeedFile } from "./bootstrap/bootstrapSchema.js";

const app = createApp({
  beforeErrorHandler: (a) => {
    if (process.env.NODE_ENV !== "development") return;
    const seedPath = path.join(process.cwd(), "..", "db", "seed.sql");
    if (fs.existsSync(seedPath)) {
      a.post("/dev/seed", async (_req, res) => {
        try {
          const { executed } = await runSeedFile();
          res.json({ ok: true, executed });
        } catch (err) {
          res.status(500).json({ ok: false, error: String(err) });
        }
      });
    }
  },
});

const PORT = process.env.PORT ?? 3000;

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
