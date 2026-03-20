-- =============================================================================
-- Migración 002: UNIQUE index para dedup en attendance_submissions
-- =============================================================================
-- Garantiza idempotencia a nivel DB: (device_id, employee_id, date) único.
-- Antes de crear el índice, elimina duplicados conservando el más antiguo.
--
-- CÓMO APLICAR:
--   turso db shell staffaxis < db/migrations/002_attendance_submissions_dedup_unique.sql
--   O desde shell interactivo: .read db/migrations/002_attendance_submissions_dedup_unique.sql
-- =============================================================================

-- 1) Eliminar duplicados: conservar 1 fila por (device_id, employee_id, date)
--    Usa MIN(created_at) para mantener el más antiguo; tiebreak por rowid
CREATE TEMP TABLE _dedup_keep AS
SELECT id FROM attendance_submissions a
WHERE NOT EXISTS (
  SELECT 1 FROM attendance_submissions b
  WHERE b.device_id = a.device_id
    AND b.employee_id = a.employee_id
    AND b.date = a.date
    AND (b.created_at < a.created_at
         OR (b.created_at = a.created_at AND b.rowid < a.rowid))
);

DELETE FROM attendance_submissions
WHERE id NOT IN (SELECT id FROM _dedup_keep);

DROP TABLE _dedup_keep;

-- 2) Crear índice UNIQUE para evitar duplicados futuros
CREATE UNIQUE INDEX IF NOT EXISTS ux_attendance_submissions_dedup
ON attendance_submissions(device_id, employee_id, date);
