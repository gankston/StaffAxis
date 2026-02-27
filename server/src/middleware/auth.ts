import type { Request, Response, NextFunction } from "express";
import { verifyAdminToken } from "../utils/security.js";
import { verifyDeviceTokenHash } from "../utils/security.js";
import { query } from "../db/turso.js";
import { AppError } from "./errorHandler.js";

/** Header: Authorization: Bearer <JWT> */
export function requireAdminAuth(
  req: Request,
  _res: Response,
  next: NextFunction
): void {
  try {
    const auth = req.headers.authorization;
    if (!auth?.startsWith("Bearer ")) {
      throw new AppError(401, "Token de admin requerido");
    }
    const token = auth.slice(7);
    req.adminUser = verifyAdminToken(token);
    next();
  } catch (err) {
    if (err instanceof AppError) next(err);
    else next(new AppError(401, "Token de admin inválido"));
  }
}

/** Header: X-Device-Token: <api_token> — valida token y setea req.deviceAuth con device_id + sector_id */
export async function requireDeviceAuth(
  req: Request,
  _res: Response,
  next: NextFunction
): Promise<void> {
  try {
    const token = req.headers["x-device-token"];
    if (typeof token !== "string" || !token.startsWith("sk_")) {
      throw new AppError(401, "Token de dispositivo requerido (X-Device-Token)");
    }

    const devices = await query(
      "SELECT id, device_id, sector_id, encargado_name, api_token_hash FROM devices"
    );

    for (const row of devices.rows) {
      const r = row as Record<string, unknown>;
      const hash = r.api_token_hash as string;
      const match = await verifyDeviceTokenHash(token, hash);
      if (match) {
        req.deviceAuth = {
          device_id: r.device_id as string,
          sector_id: r.sector_id as string,
          encargado_name: r.encargado_name as string,
          device_db_id: r.id as string,
        };
        return next();
      }
    }

    throw new AppError(401, "Token de dispositivo inválido");
  } catch (err) {
    if (err instanceof AppError) next(err);
    else next(new AppError(401, "Token de dispositivo inválido"));
  }
}
