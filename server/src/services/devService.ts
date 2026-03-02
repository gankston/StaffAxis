import { db } from "../db/turso.js";

const SECTOR_ID = "sector-dev-001";
const EMPLOYEE_ID = "emp-dev-001";

export async function seedMin() {
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
  return {
    sector: { id: SECTOR_ID, name: "Sector Dev" },
    employee: { id: EMPLOYEE_ID, sector_id: SECTOR_ID },
  };
}
