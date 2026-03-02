import { db } from "../db/turso.js";
import { AppError } from "../middleware/errorHandler.js";

export async function listSectors() {
  const result = await db.execute({
    sql: "SELECT id, name FROM sectors ORDER BY name",
    args: [],
  });
  return { sectors: result.rows };
}

export async function createSector(body: { name: string }) {
  const { name } = body;
  const id = crypto.randomUUID();
  try {
    await db.execute({
      sql: "INSERT INTO sectors (id, name) VALUES (?, ?)",
      args: [id, name],
    });
  } catch (err) {
    const msg = err instanceof Error ? err.message : String(err);
    if (msg.includes("UNIQUE") || msg.includes("unique")) {
      throw new AppError(409, "Sector con ese nombre ya existe", "conflict");
    }
    throw err;
  }
  return { id, name, status: 201 as const };
}
