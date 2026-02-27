import { Router } from "express";
import { z } from "zod";
import { db } from "../db/turso.js";
import {
  verifyPassword,
  generateAdminToken,
  generateDeviceToken,
  hashDeviceToken,
} from "../utils/security.js";
import { validate } from "../middleware/validate.js";
import { requireAdminAuth, requireDeviceAuth } from "../middleware/auth.js";
import { AppError } from "../middleware/errorHandler.js";

export const authRouter = Router();

const loginSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

async function adminLoginHandler(req: import("express").Request, res: import("express").Response): Promise<void> {
  const { username, password } = req.body as z.infer<typeof loginSchema>;
  const result = await db.execute({
    sql: "SELECT id, username, password_hash FROM admin_users WHERE username = ?",
    args: [username],
  });

  const row = result.rows[0] as Record<string, unknown> | undefined;
  if (!row) {
    throw new AppError(401, "Credenciales inválidas");
  }

  const match = await verifyPassword(password, row.password_hash as string);
  if (!match) {
    throw new AppError(401, "Credenciales inválidas");
  }

  const token = generateAdminToken({
    sub: row.id as string,
    username: row.username as string,
  });
  res.json({ token });
}

/** POST /api/auth/login — admin login, retorna JWT */
authRouter.post("/login", validate(loginSchema), adminLoginHandler);

/** POST /api/auth/admin/login — admin login, retorna JWT */
authRouter.post("/admin/login", validate(loginSchema), adminLoginHandler);

/** GET /api/auth/me — requiere JWT, retorna admin actual */
authRouter.get("/me", requireAdminAuth, (req, res) => {
  res.json({ admin: req.adminUser });
});

/** POST /api/auth/device/register — crear o actualizar device, retorna token (solo hash en DB) */
const registerDeviceSchema = z.object({
  device_id: z.string().min(1),
  sector_id: z.string().min(1),
  encargado_name: z.string().min(1),
});
authRouter.post(
  "/device/register",
  validate(registerDeviceSchema),
  async (req, res) => {
    const { device_id, sector_id, encargado_name } = req.body as z.infer<
      typeof registerDeviceSchema
    >;

    const rawToken = generateDeviceToken();
    const apiTokenHash = await hashDeviceToken(rawToken);
    const now = Math.floor(Date.now() / 1000);

    const existing = await db.execute({
      sql: "SELECT id FROM devices WHERE device_id = ?",
      args: [device_id],
    });

    if (existing.rows.length === 0) {
      const id = crypto.randomUUID();
      await db.execute({
        sql: `
          INSERT INTO devices (id, device_id, sector_id, encargado_name, api_token_hash, created_at, updated_at)
          VALUES (?, ?, ?, ?, ?, ?, ?)
        `,
        args: [id, device_id, sector_id, encargado_name, apiTokenHash, now, now],
      });
    } else {
      await db.execute({
        sql: `
          UPDATE devices SET sector_id = ?, encargado_name = ?, api_token_hash = ?, updated_at = ? WHERE device_id = ?
        `,
        args: [sector_id, encargado_name, apiTokenHash, now, device_id],
      });
    }

    res.json({ token: rawToken });
  }
);

/** POST /api/auth/device/login — rotar token si device existe, 404 si no */
const deviceLoginSchema = z.object({
  device_id: z.string().min(1),
});
authRouter.post(
  "/device/login",
  validate(deviceLoginSchema),
  async (req, res) => {
    const { device_id } = req.body as z.infer<typeof deviceLoginSchema>;

    const existing = await db.execute({
      sql: "SELECT id FROM devices WHERE device_id = ?",
      args: [device_id],
    });
    if (existing.rows.length === 0) {
      throw new AppError(404, "Dispositivo no encontrado");
    }

    const rawToken = generateDeviceToken();
    const apiTokenHash = await hashDeviceToken(rawToken);
    const now = Math.floor(Date.now() / 1000);
    await db.execute({
      sql: "UPDATE devices SET api_token_hash = ?, updated_at = ? WHERE device_id = ?",
      args: [apiTokenHash, now, device_id],
    });

    res.json({ token: rawToken });
  }
);

/** GET /api/auth/device/ping — requiere device token (X-Device-Token), retorna device_id + sector_id */
/** GET /api/auth/device/ping — requiere X-Device-Token, retorna device_id + sector_id + encargado_name */
authRouter.get("/device/ping", requireDeviceAuth, (req, res) => {
  res.json({ device: req.deviceAuth });
});
