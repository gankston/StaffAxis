import type { Request, Response, NextFunction } from "express";
import type { ZodSchema } from "zod";
import { AppError } from "./errorHandler.js";

function parseOrThrow(schema: ZodSchema, data: unknown, source: string): unknown {
  const result = schema.safeParse(data);
  if (!result.success) {
    const messages = result.error.flatten().fieldErrors;
    const first = Object.values(messages).flat()[0];
    throw new AppError(400, (first as string) ?? "Datos inválidos", "validation_error");
  }
  return result.data;
}

/** Valida req.body */
export function validate(schema: ZodSchema) {
  return (req: Request, _res: Response, next: NextFunction): void => {
    req.body = parseOrThrow(schema, req.body ?? {}, "body");
    next();
  };
}

/** Valida req.query y guarda resultado tipado en req.validatedQuery */
export function validateQuery<T>(schema: ZodSchema<T>) {
  return (req: Request, _res: Response, next: NextFunction): void => {
    const parsed = parseOrThrow(schema, req.query ?? {}, "query") as T;
    req.validatedQuery = parsed as Record<string, unknown>;
    next();
  };
}

/** Valida req.params */
export function validateParams(schema: ZodSchema) {
  return (req: Request, _res: Response, next: NextFunction): void => {
    req.params = parseOrThrow(schema, req.params ?? {}, "params") as Request["params"];
    next();
  };
}
