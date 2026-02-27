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

/** POST /api/auth/login — admin login, retorna JWT */
authRouter.post("/login", validate(loginSchema), async (req, res) => {
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
});

/** GET /api/auth/me — requiere JWT, retorna admin actual */
authRouter.get("/me", requireAdminAuth, (req, res) => {
  res.json({ admin: req.adminUser });
});

/** POST /api/auth/device/register — requiere admin JWT, crea device y retorna token (una sola vez) */
const registerDeviceSchema = z.object({
  device_id: z.string().min(1),
  sector_id: z.string().min(1),
  encargado_name: z.string().min(1),
});
authRouter.post(
  "/device/register",
  requireAdminAuth,
  validate(registerDeviceSchema),
  async (req, res) => {
    const { device_id, sector_id, encargado_name } = req.body as z.infer<
      typeof registerDeviceSchema
    >;

    const existing = await db.execute({
      sql: "SELECT id FROM devices WHERE device_id = ? AND sector_id = ?",
      args: [device_id, sector_id],
    });
    if (existing.rows.length > 0) {
      throw new AppError(400, "Dispositivo ya registrado en ese sector");
    }

    const rawToken = generateDeviceToken();
    const apiTokenHash = await hashDeviceToken(rawToken);
    const id = crypto.randomUUID();
    const now = Math.floor(Date.now() / 1000);

    await db.execute({
      sql: `
        INSERT INTO devices (id, device_id, sector_id, encargado_name, api_token_hash, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
      `,
      args: [id, device_id, sector_id, encargado_name, apiTokenHash, now, now],
    });

    res.status(201).json({
      token: rawToken,
      device_id,
      sector_id,
      message: "Guardá el token: solo se muestra una vez",
    });
  }
);

/** GET /api/auth/device/ping — requiere device token, retorna device_id + sector_id */
authRouter.get("/device/ping", requireDeviceAuth, (req, res) => {
  res.json({ device: req.deviceAuth });
});
