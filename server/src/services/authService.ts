import { db, query } from "../db/turso.js";
import {
  verifyPassword,
  generateAdminToken,
  generateDeviceToken,
  hashDeviceToken,
  hashPassword,
  verifyAdminToken as verifyJwt,
  verifyDeviceTokenHash,
  type AdminTokenPayload,
} from "../utils/security.js";
import { AppError } from "../middleware/errorHandler.js";

export type DeviceAuth = {
  device_id: string;
  sector_id: string;
  encargado_name: string;
  device_db_id: string;
};

export async function adminLogin(body: { username: string; password: string }) {
  const { username, password } = body;
  const result = await db.execute({
    sql: "SELECT id, username, password_hash FROM admin_users WHERE username = ?",
    args: [username],
  });
  const row = result.rows[0] as Record<string, unknown> | undefined;
  if (!row) {
    throw new AppError(401, "Credenciales inválidas", "unauthorized");
  }
  const match = await verifyPassword(password, row.password_hash as string);
  if (!match) {
    throw new AppError(401, "Credenciales inválidas", "unauthorized");
  }
  const token = generateAdminToken({
    sub: row.id as string,
    username: row.username as string,
  });
  return { token };
}

export async function registerDevice(body: {
  device_id: string;
  sector_id: string;
  encargado_name: string;
}) {
  const { device_id, sector_id, encargado_name } = body;
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
      sql: `INSERT INTO devices (id, device_id, sector_id, encargado_name, api_token_hash, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)`,
      args: [id, device_id, sector_id, encargado_name, apiTokenHash, now, now],
    });
  } else {
    await db.execute({
      sql: `UPDATE devices SET sector_id = ?, encargado_name = ?, api_token_hash = ?, updated_at = ? WHERE device_id = ?`,
      args: [sector_id, encargado_name, apiTokenHash, now, device_id],
    });
  }
  return { token: rawToken };
}

export async function deviceLogin(body: { device_id: string }) {
  const { device_id } = body;
  const existing = await db.execute({
    sql: "SELECT id FROM devices WHERE device_id = ?",
    args: [device_id],
  });
  if (existing.rows.length === 0) {
    throw new AppError(404, "Dispositivo no encontrado", "not_found");
  }
  const rawToken = generateDeviceToken();
  const apiTokenHash = await hashDeviceToken(rawToken);
  const now = Math.floor(Date.now() / 1000);
  await db.execute({
    sql: "UPDATE devices SET api_token_hash = ?, updated_at = ? WHERE device_id = ?",
    args: [apiTokenHash, now, device_id],
  });
  return { token: rawToken };
}

export function getAdminFromBearer(authHeader: string | null | undefined): AdminTokenPayload {
  if (!authHeader?.startsWith("Bearer ")) {
    throw new AppError(401, "Token de admin requerido", "unauthorized");
  }
  const token = authHeader.slice(7);
  try {
    return verifyJwt(token);
  } catch {
    throw new AppError(401, "Token de admin inválido", "unauthorized");
  }
}

export async function getDeviceFromToken(token: string | null | undefined): Promise<DeviceAuth> {
  if (typeof token !== "string" || !token.startsWith("sk_")) {
    throw new AppError(401, "Token de dispositivo requerido (X-Device-Token)", "unauthorized");
  }
  const devices = await query(
    "SELECT id, device_id, sector_id, encargado_name, api_token_hash FROM devices"
  );
  for (const row of devices.rows) {
    const r = row as Record<string, unknown>;
    const hash = r.api_token_hash as string;
    const match = await verifyDeviceTokenHash(token, hash);
    if (match) {
      return {
        device_id: r.device_id as string,
        sector_id: r.sector_id as string,
        encargado_name: r.encargado_name as string,
        device_db_id: r.id as string,
      };
    }
  }
  throw new AppError(401, "Token de dispositivo inválido", "unauthorized");
}

/**
 * Resetea la contraseña de un admin. Si no existe, lo crea.
 * La validación de ADMIN_RESET_KEY y header se hace en la ruta.
 * NO loguear passwords ni reset key.
 */
export async function resetAdminPassword(body: {
  username: string;
  new_password: string;
}): Promise<{ ok: true }> {
  const { username, new_password } = body;
  const passwordHash = await hashPassword(new_password);
  const now = Math.floor(Date.now() / 1000);

  const existing = await db.execute({
    sql: "SELECT id FROM admin_users WHERE username = ?",
    args: [username],
  });

  if (existing.rows.length > 0) {
    await db.execute({
      sql: "UPDATE admin_users SET password_hash = ?, updated_at = ? WHERE username = ?",
      args: [passwordHash, now, username],
    });
  } else {
    const id = crypto.randomUUID();
    await db.execute({
      sql: `INSERT INTO admin_users (id, username, password_hash, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)`,
      args: [id, username, passwordHash, now, now],
    });
  }
  return { ok: true };
}
