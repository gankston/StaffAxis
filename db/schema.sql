-- =============================================================================
-- StaffAxis - Schema central (libSQL/Turso)
-- =============================================================================
-- Timestamps: INTEGER (unix epoch) para created_at, updated_at, deleted_at
-- Fechas de negocio: TEXT "yyyy-MM-dd"
-- PKs: UUID TEXT (generados con hex(randomblob(16)) o desde app)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- sectors
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sectors (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL UNIQUE,
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE UNIQUE INDEX idx_sectors_name ON sectors(name);

-- -----------------------------------------------------------------------------
-- employees
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id              TEXT PRIMARY KEY,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    legajo          TEXT,                    -- DNI, nullable
    nombre_completo TEXT NOT NULL,
    fecha_ingreso   TEXT NOT NULL,           -- "yyyy-MM-dd"
    activo          INTEGER NOT NULL DEFAULT 1,
    observacion     TEXT,
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE INDEX idx_employees_sector ON employees(sector_id);
CREATE INDEX idx_employees_legajo ON employees(legajo);
CREATE UNIQUE INDEX idx_employees_sector_legajo ON employees(sector_id, legajo) WHERE legajo IS NOT NULL;

-- -----------------------------------------------------------------------------
-- attendance_submissions (pendientes desde móviles, pre-aprobación)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance_submissions (
    id              TEXT PRIMARY KEY,
    employee_id     TEXT NOT NULL REFERENCES employees(id),
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    fecha           TEXT NOT NULL,           -- "yyyy-MM-dd"
    horas_trabajadas INTEGER NOT NULL CHECK (horas_trabajadas IN (4, 8, 12)),
    observaciones   TEXT,
    device_id       TEXT,
    status          TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE INDEX idx_attendance_submissions_employee ON attendance_submissions(employee_id);
CREATE INDEX idx_attendance_submissions_sector ON attendance_submissions(sector_id);
CREATE INDEX idx_attendance_submissions_fecha ON attendance_submissions(fecha);
CREATE INDEX idx_attendance_submissions_status ON attendance_submissions(status);
CREATE UNIQUE INDEX idx_attendance_submissions_employee_fecha ON attendance_submissions(employee_id, fecha) WHERE status = 'pending';

-- -----------------------------------------------------------------------------
-- attendances (oficial, aprobado)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendances (
    id              TEXT PRIMARY KEY,
    employee_id     TEXT NOT NULL REFERENCES employees(id),
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    fecha           TEXT NOT NULL,           -- "yyyy-MM-dd"
    horas_trabajadas INTEGER NOT NULL CHECK (horas_trabajadas IN (4, 8, 12)),
    observaciones   TEXT,
    deleted_at      INTEGER,                -- soft delete (NULL = activo)
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE INDEX idx_attendances_employee ON attendances(employee_id);
CREATE INDEX idx_attendances_sector ON attendances(sector_id);
CREATE INDEX idx_attendances_fecha ON attendances(fecha);
CREATE INDEX idx_attendances_deleted ON attendances(deleted_at);
CREATE UNIQUE INDEX idx_attendances_employee_fecha_active ON attendances(employee_id, fecha) WHERE deleted_at IS NULL;

-- -----------------------------------------------------------------------------
-- admin_users (encargados / administradores)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin_users (
    id              TEXT PRIMARY KEY,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    nombre          TEXT NOT NULL,
    email           TEXT,
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE INDEX idx_admin_users_sector ON admin_users(sector_id);

-- -----------------------------------------------------------------------------
-- device_tokens (dispositivos registrados por sector)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS device_tokens (
    id              TEXT PRIMARY KEY,
    device_id       TEXT NOT NULL,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    admin_user_id   TEXT REFERENCES admin_users(id),
    created_at      INTEGER NOT NULL DEFAULT (unixepoch()),
    updated_at      INTEGER NOT NULL DEFAULT (unixepoch())
);

CREATE UNIQUE INDEX idx_device_tokens_device_sector ON device_tokens(device_id, sector_id);
CREATE INDEX idx_device_tokens_sector ON device_tokens(sector_id);

-- =============================================================================
-- CÓMO EJECUTAR EN TURSO
-- =============================================================================
--
-- 1. Instalar Turso CLI: https://docs.turso.tech/cli/install
--
-- 2. Crear base de datos (si no existe):
--    turso db create staffaxis
--
-- 3. Ejecutar schema desde archivo:
--    turso db shell staffaxis < db/schema.sql
--
-- 4. O conectarse interactivamente y pegar el contenido:
--    turso db shell staffaxis
--    .read db/schema.sql
--
-- 5. Verificar tablas:
--    turso db shell staffaxis
--    .tables
--
-- =============================================================================
