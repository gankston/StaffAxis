import { db } from "../db/turso.js";
import { logger } from "../utils/logger.js";

/**
 * Crear admin inicial si no existe, usando ADMIN_BOOTSTRAP_USER y ADMIN_BOOTSTRAP_PASS.
 * El hash debe generarse con bcrypt/argon2 en producción.
 */
export async function bootstrapAdmin(): Promise<void> {
  const user = process.env.ADMIN_BOOTSTRAP_USER;
  const pass = process.env.ADMIN_BOOTSTRAP_PASS;

  if (!user || !pass) {
    logger.warn("ADMIN_BOOTSTRAP_USER o ADMIN_BOOTSTRAP_PASS no configurados. Saltando bootstrap.");
    return;
  }

  try {
    const existing = await db.execute({
      sql: "SELECT id FROM admin_users WHERE username = ?",
      args: [user],
    });

    if (existing.rows.length > 0) {
      logger.info("Admin ya existe, omitiendo bootstrap.");
      return;
    }

    // TODO: usar bcrypt.hash(pass, 10) en producción
    // Por ahora placeholder - reemplazar por hash real
    const passwordHash = `placeholder_${Buffer.from(pass).toString("base64")}`;

    const id = crypto.randomUUID();
    const now = Math.floor(Date.now() / 1000);
    await db.execute({
      sql: `
        INSERT INTO admin_users (id, username, password_hash, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?)
      `,
      args: [id, user, passwordHash, now, now],
    });

    logger.info("Admin bootstrap creado:", user);
  } catch (err) {
    logger.error("Error en bootstrap admin:", err);
    // No lanzar - permitir que el servidor arranque aunque falle el bootstrap
  }
}
