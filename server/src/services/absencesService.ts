import { db } from "../db/turso.js";
import { logger } from "../utils/logger.js";

/**
 * Crea una nueva ausencia en la base de datos.
 */
export async function createAbsence(
  data: {
    employee_id: string;
    start_date: string;
    end_date: string;
    reason?: string | null;
    observations?: string | null;
    is_justified?: boolean | number;
  },
  sector_id: string
) {
  const { employee_id, start_date, end_date, reason = null, observations = null, is_justified = 0 } = data;
  
  // Convert boolean to number for SQLite
  const justified = is_justified === true || is_justified === 1 ? 1 : 0;

  const id = crypto.randomUUID();

  try {
    await db.execute({
      sql: `INSERT INTO absences 
            (id, employee_id, sector_id, start_date, end_date, reason, observations, is_justified)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      args: [id, employee_id, sector_id, start_date, end_date, reason, observations, justified]
    });
    return { ok: true, id };
  } catch (err) {
    logger.error("Error creating absence:", err);
    throw err;
  }
}

/**
 * Lista ausencias sin JOIN (el cliente Android solo necesita employee_id para comparar).
 * Usar LEFT JOIN o no joinear para no perder filas cuando employee_id no está en employees.
 */
export async function listAbsences(filters: {
  sectorId?: string;
  startDate?: string;
  endDate?: string;
}) {
  const { sectorId, startDate, endDate } = filters;
  
  let sql = `
    SELECT 
      a.id,
      a.employee_id,
      a.sector_id,
      a.start_date,
      a.end_date,
      a.reason,
      a.observations,
      a.is_justified,
      a.created_at
    FROM absences a
    WHERE 1=1
  `;
  
  const args: any[] = [];
  
  if (sectorId) {
    sql += " AND a.sector_id = ?";
    args.push(sectorId);
  }
  
  if (startDate) {
    sql += " AND a.start_date <= ?";
    args.push(endDate ?? startDate); // end_date de la ausencia >= start_date del rango
  }

  if (endDate) {
    sql += " AND a.end_date >= ?";
    args.push(startDate ?? endDate); // start_date de la ausencia <= end_date del rango
  }
  
  sql += " ORDER BY a.start_date DESC";
  
  try {
    const result = await db.execute({ sql, args });
    logger.info(`listAbsences: sectorId=${sectorId} startDate=${startDate} endDate=${endDate} → ${result.rows.length} rows`);
    return { absences: result.rows };
  } catch (err) {
    logger.error("Error listing absences:", err);
    throw err;
  }
}

