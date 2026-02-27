import "dotenv/config";
import express from "express";
import { router } from "./routes/index.js";
import { errorHandler } from "./middleware/errorHandler.js";
import { logger } from "./utils/logger.js";
import { bootstrapAdmin } from "./services/bootstrap.js";

const PORT = process.env.PORT ?? 3000;

const app = express();

app.use(express.json());

// Logging básico
app.use((req, _res, next) => {
  logger.info(`${req.method} ${req.path}`);
  next();
});

app.use("/api", router);

app.get("/", (_req, res) => {
  res.json({ name: "StaffAxis API", version: "1.0.0" });
});

app.use(errorHandler);

async function start() {
  await bootstrapAdmin().catch(() => {});
  app.listen(PORT, () => {
    logger.info(`Servidor en http://localhost:${PORT}`);
  });
}

start();
