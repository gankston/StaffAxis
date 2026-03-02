import * as fs from "fs";
import * as path from "path";
import { db } from "../db/turso.js";
import { logger } from "../utils/logger.js";
import { schemaSql as embeddedSchemaSql } from "./schemaContent.js";

/**
 * Verifica si existe la tabla attendances (indica que el schema ya fue aplicado).
 */
async function schemaExists(): Promise<boolean> {
  try {
    const result = await db.execute({
      sql: "SELECT name FROM sqlite_master WHERE type='table' AND name='attendances'",
      args: [],
    });
    return result.rows.length > 0;
  } catch {
    return false;
  }
}

/**
 * Obtiene el schema SQL: desde archivo (Node) o embebido (Workers).
 */
function getSchemaSql(): string | null {
  try {
    const schemaPath = path.join(process.cwd(), "..", "db", "schema.sql");
    if (typeof fs !== "undefined" && fs.existsSync?.(schemaPath)) {
      return fs.readFileSync(schemaPath, "utf-8");
    }
  } catch {
    /* fs no disponible (Workers) */
  }
  return embeddedSchemaSql;
}

/**
 * Ejecuta el schema.sql si la tabla attendances no existe.
 * En Node: lee db/schema.sql. En Workers: usa schema embebido.
 */
export async function bootstrapSchema(): Promise<void> {
  if (await schemaExists()) {
    logger.info("Schema already present");
    return;
  }

  const schemaSql = getSchemaSql();
  if (!schemaSql) {
    logger.warn("Bootstrap schema: no se pudo cargar el schema");
    return;
  }

  try {
    await db.executeMultiple(schemaSql);
    logger.info("Schema initialized successfully");
  } catch (err) {
    logger.error("Bootstrap schema failed:", err);
    throw err;
  }
}

/**
 * Ejecuta un archivo SQL (para seed). Divide por ; y ejecuta cada statement.
 * No maneja triggers complejos; pensado para seed con INSERT.
 */
function splitSeedStatements(sql: string): string[] {
  return sql
    .split(/;\s*\n/)
    .map((s) => s.trim())
    .filter((s) => s.length > 0 && !s.startsWith("--"));
}

/**
 * Ejecuta db/seed.sql. Usado por el endpoint /dev/seed.
 */
export async function runSeedFile(): Promise<{ executed: number }> {
  const seedPath = path.join(process.cwd(), "..", "db", "seed.sql");
  if (!fs.existsSync(seedPath)) {
    throw new Error("db/seed.sql no encontrado");
  }
  const sql = fs.readFileSync(seedPath, "utf-8");
  const statements = splitSeedStatements(sql).map((s) => (s.endsWith(";") ? s : s + ";"));
  for (let i = 0; i < statements.length; i++) {
    const stmt = statements[i].trim();
    if (!stmt || stmt === ";") continue;
    await db.execute({ sql: stmt, args: [] });
  }
  return { executed: statements.length };
}
