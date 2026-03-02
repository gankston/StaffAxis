-- =============================================================================
-- Migración 001: Asegurar employees.id como TEXT PRIMARY KEY (para DNI)
-- =============================================================================
-- USO: Solo ejecutar si tu tabla employees tiene id como INTEGER u otro tipo.
--      El schema actual (db/schema.sql) ya define id TEXT PRIMARY KEY.
--
-- VERIFICAR PRIMERO en Turso console:
--   PRAGMA table_info(employees);
--   Si la columna "id" muestra type="TEXT", NO ejecutar esta migración.
--
-- CÓMO APLICAR:
--   turso db shell staffaxis < db/migrations/001_employees_id_text.sql
--   O desde shell interactivo: .read db/migrations/001_employees_id_text.sql
-- =============================================================================

PRAGMA foreign_keys = OFF;

-- Crear tabla nueva con schema correcto
CREATE TABLE IF NOT EXISTS employees_new (
    id              TEXT PRIMARY KEY,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    first_name      TEXT NOT NULL,
    last_name       TEXT NOT NULL,
    external_code   TEXT NULL,
    is_active       INTEGER NOT NULL DEFAULT 1,
    created_at      INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL
);

-- Copiar datos (CAST id a TEXT por si era INTEGER)
INSERT OR IGNORE INTO employees_new (id, sector_id, first_name, last_name, external_code, is_active, created_at, updated_at)
SELECT CAST(id AS TEXT), sector_id, first_name, last_name, external_code, is_active, created_at, updated_at
FROM employees;

-- Eliminar tabla vieja
DROP TABLE IF EXISTS employees;

-- Renombrar nueva a employees
ALTER TABLE employees_new RENAME TO employees;

-- Recrear índices
CREATE INDEX IF NOT EXISTS idx_employees_sector_id ON employees(sector_id);
CREATE INDEX IF NOT EXISTS idx_employees_is_active ON employees(is_active);

-- Recrear trigger
CREATE TRIGGER IF NOT EXISTS employees_updated_at
BEFORE UPDATE ON employees
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE employees SET updated_at = unixepoch() WHERE id = NEW.id;
END;

PRAGMA foreign_keys = ON;
