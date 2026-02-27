-- =============================================================================
-- StaffAxis - Schema central (libSQL/Turso)
-- =============================================================================
-- Timestamps: INTEGER (unix epoch)
-- Fechas: TEXT "YYYY-MM-DD", horas: TEXT "HH:MM"
-- PKs: UUID TEXT
-- =============================================================================

-- -----------------------------------------------------------------------------
-- SECTORS
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sectors (
    id              TEXT PRIMARY KEY,
    name            TEXT NOT NULL UNIQUE
);

-- -----------------------------------------------------------------------------
-- EMPLOYEES
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id              TEXT PRIMARY KEY,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    first_name      TEXT NOT NULL,
    last_name       TEXT NOT NULL,
    external_code   TEXT NULL,              -- legajo / DNI
    is_active       INTEGER NOT NULL DEFAULT 1,
    created_at      INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL
);

CREATE INDEX idx_employees_sector_id ON employees(sector_id);
CREATE INDEX idx_employees_is_active ON employees(is_active);

-- -----------------------------------------------------------------------------
-- ADMIN_USERS (debe existir antes de attendances por FK approved_by_admin_id)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin_users (
    id              TEXT PRIMARY KEY,
    username        TEXT NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    created_at      INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL
);

-- -----------------------------------------------------------------------------
-- DEVICES
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS devices (
    id              TEXT PRIMARY KEY,
    device_id       TEXT NOT NULL UNIQUE,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    encargado_name  TEXT NOT NULL,
    api_token_hash  TEXT NOT NULL,
    created_at      INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL
);

CREATE INDEX idx_devices_sector_id ON devices(sector_id);

-- -----------------------------------------------------------------------------
-- ATTENDANCE_SUBMISSIONS (pendientes desde móviles)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance_submissions (
    id              TEXT PRIMARY KEY,
    device_id       TEXT NOT NULL,
    sector_id       TEXT NOT NULL REFERENCES sectors(id),
    encargado_name  TEXT NOT NULL,
    employee_id     TEXT NOT NULL REFERENCES employees(id),
    date            TEXT NOT NULL,          -- YYYY-MM-DD
    minutes_worked  INTEGER NULL,
    check_in        TEXT NULL,              -- HH:MM
    check_out       TEXT NULL,              -- HH:MM
    notes           TEXT NULL,
    status          TEXT NOT NULL CHECK(status IN ('pending','approved','rejected')) DEFAULT 'pending',
    created_at      INTEGER NOT NULL,
    updated_at      INTEGER NOT NULL,
    UNIQUE(device_id, employee_id, date, check_in, check_out, minutes_worked)
);

CREATE INDEX idx_attendance_submissions_status_sector_date ON attendance_submissions(status, sector_id, date);
CREATE INDEX idx_attendance_submissions_employee_date ON attendance_submissions(employee_id, date);
CREATE INDEX idx_attendance_submissions_updated_at ON attendance_submissions(updated_at);

-- -----------------------------------------------------------------------------
-- ATTENDANCES (oficial, aprobado)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendances (
    id                      TEXT PRIMARY KEY,
    employee_id             TEXT NOT NULL REFERENCES employees(id),
    sector_id               TEXT NOT NULL REFERENCES sectors(id),
    date                    TEXT NOT NULL,  -- YYYY-MM-DD
    minutes_worked          INTEGER NULL,
    check_in                TEXT NULL,      -- HH:MM
    check_out               TEXT NULL,      -- HH:MM
    notes                   TEXT NULL,
    approved_from_submission_id TEXT NULL REFERENCES attendance_submissions(id),
    approved_by_admin_id    TEXT NULL REFERENCES admin_users(id),
    created_at              INTEGER NOT NULL,
    updated_at              INTEGER NOT NULL,
    deleted_at              INTEGER NULL,
    UNIQUE(employee_id, date)
);

CREATE INDEX idx_attendances_sector_date ON attendances(sector_id, date);
CREATE INDEX idx_attendances_employee_date ON attendances(employee_id, date);
CREATE INDEX idx_attendances_updated_at ON attendances(updated_at);
CREATE INDEX idx_attendances_deleted_at ON attendances(deleted_at);

-- =============================================================================
-- TRIGGERS: mantener updated_at automáticamente
-- =============================================================================
-- Si no se usan triggers, el backend DEBE actualizar updated_at en cada UPDATE.
-- Alternativa: manejar updated_at en la capa de aplicación.
-- =============================================================================

CREATE TRIGGER IF NOT EXISTS employees_updated_at
BEFORE UPDATE ON employees
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE employees SET updated_at = unixepoch() WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS admin_users_updated_at
BEFORE UPDATE ON admin_users
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE admin_users SET updated_at = unixepoch() WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS devices_updated_at
BEFORE UPDATE ON devices
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE devices SET updated_at = unixepoch() WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS attendance_submissions_updated_at
BEFORE UPDATE ON attendance_submissions
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE attendance_submissions SET updated_at = unixepoch() WHERE id = NEW.id;
END;

CREATE TRIGGER IF NOT EXISTS attendances_updated_at
BEFORE UPDATE ON attendances
FOR EACH ROW
WHEN NEW.updated_at = OLD.updated_at
BEGIN
    UPDATE attendances SET updated_at = unixepoch() WHERE id = NEW.id;
END;

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
-- 4. O conectarse interactivamente:
--    turso db shell staffaxis
--    .read db/schema.sql
--
-- 5. Verificar tablas:
--    .tables
--
-- =============================================================================
