import { db } from "../db/turso.js";
import { hashPassword } from "../utils/security.js";
import { logger } from "../utils/logger.js";

/**
 * Si no existe ningún admin, crea uno con ADMIN_BOOTSTRAP_USER y ADMIN_BOOTSTRAP_PASS.
 * Password hasheado con bcrypt.
 */
export async function bootstrapAdmin(): Promise<void> {
  const user = process.env.ADMIN_BOOTSTRAP_USER;
  const pass = process.env.ADMIN_BOOTSTRAP_PASS;

  if (!user || !pass) {
    logger.warn("Bootstrap admin: variables no configuradas.");
    return;
  }

  try {
    const count = await db.execute({
      sql: "SELECT COUNT(*) as n FROM admin_users",
      args: [],
    });
    const row = count.rows[0] as Record<string, unknown> | undefined;
    const n = Number(row?.["n"] ?? 0);
    if (n > 0) {
      logger.info("Bootstrap admin: ya existen usuarios.");
      return;
    }

    const passwordHash = await hashPassword(pass);

    const id = crypto.randomUUID();
    const now = Math.floor(Date.now() / 1000);
    await db.execute({
      sql: `
        INSERT INTO admin_users (id, username, password_hash, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?)
      `,
      args: [id, user, passwordHash, now, now],
    });

    logger.info("Bootstrap admin: usuario inicial creado.");
  } catch (err) {
    logger.error("Bootstrap admin: error al crear usuario.", err);
    // No lanzar - permitir que el servidor arranque aunque falle el bootstrap
  }
}
