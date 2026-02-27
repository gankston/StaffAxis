import type { Request, Response, NextFunction } from "express";
import { logger } from "../utils/logger.js";

export type ErrorCode =
  | "validation_error"
  | "unauthorized"
  | "forbidden"
  | "not_found"
  | "conflict"
  | "internal_error";

const CODE_BY_STATUS: Record<number, ErrorCode> = {
  400: "validation_error",
  401: "unauthorized",
  403: "forbidden",
  404: "not_found",
  409: "conflict",
};

export class AppError extends Error {
  public readonly code: ErrorCode;

  constructor(
    public statusCode: number,
    message: string,
    code?: ErrorCode,
    public isOperational = true
  ) {
    super(message);
    this.code = code ?? CODE_BY_STATUS[statusCode] ?? "internal_error";
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
    res.status(err.statusCode).json({ code: err.code, message: err.message });
    return;
  }

  if (err instanceof Error) {
    logger.error("Error no manejado:", err.message, err.stack);
    res
      .status(500)
      .json({ code: "internal_error", message: "Error interno del servidor" });
    return;
  }

  res
    .status(500)
    .json({ code: "internal_error", message: "Error desconocido" });
}
