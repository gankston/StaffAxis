import type { Request, Response, NextFunction } from "express";
import { logger } from "../utils/logger.js";

export class AppError extends Error {
  constructor(
    public statusCode: number,
    message: string,
    public isOperational = true
  ) {
    super(message);
    Object.setPrototypeOf(this, AppError.prototype);
    Error.captureStackTrace(this, this.constructor);
  }
}

export function errorHandler(
  err: unknown,
  _req: Request,
  res: Response,
  _next: NextFunction
): void {
  if (err instanceof AppError) {
    res.status(err.statusCode).json({ error: err.message });
    return;
  }

  if (err instanceof Error) {
    logger.error("Error no manejado:", err.message, err.stack);
    res.status(500).json({ error: "Error interno del servidor" });
    return;
  }

  res.status(500).json({ error: "Error desconocido" });
}
