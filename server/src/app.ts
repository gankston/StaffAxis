import express from "express";
import { router } from "./routes/index.js";
import { healthRouter } from "./routes/health.js";
import { errorHandler } from "./middleware/errorHandler.js";
import { logger } from "./utils/logger.js";

export type BeforeErrorHandler = (app: express.Express) => void;

export function createApp(opts?: { beforeErrorHandler?: BeforeErrorHandler }): express.Express {
  const app = express();

  app.use(express.json());

  app.use((req, _res, next) => {
    logger.info(`${req.method} ${req.path}`);
    next();
  });

  app.use("/api", router);
  app.use("/health", healthRouter);

  app.get("/", (_req, res) => {
    res.json({ name: "StaffAxis API", version: "1.0.0" });
  });

  opts?.beforeErrorHandler?.(app);

  app.use(errorHandler);

  return app;
}

const app = createApp();
export { app };
