import { Router } from "express";
import { db } from "../db/turso.js";

export const devRouter = Router();

const SECTOR_ID = "sector-dev-001";
const EMPLOYEE_ID = "emp-dev-001";

/**
 * POST /api/dev/seed-min
 * Solo NODE_ENV=development.
 * Inserta 1 sector y 1 empleado mínimos si no existen.
 */
devRouter.post("/seed-min", async (_req, res) => {
  try {
    const now = Math.floor(Date.now() / 1000);

    await db.execute({
      sql: "INSERT OR IGNORE INTO sectors (id, name) VALUES (?, ?)",
      args: [SECTOR_ID, "Sector Dev"],
    });

    await db.execute({
      sql: `INSERT OR IGNORE INTO employees (id, sector_id, first_name, last_name, external_code, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      args: [EMPLOYEE_ID, SECTOR_ID, "Juan", "Perez", "1001", 1, now, now],
    });

    res.json({
      sector: { id: SECTOR_ID, name: "Sector Dev" },
      employee: { id: EMPLOYEE_ID, sector_id: SECTOR_ID },
    });
  } catch (err) {
    res.status(500).json({ error: String(err) });
  }
});
