-- =============================================================================
-- StaffAxis - Datos iniciales (seed) para libSQL/Turso
-- =============================================================================
-- Ejecutar DESPUÉS de schema.sql
-- Comando: turso db shell staffaxis < db/seed.sql
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1) SECTORES (3 sectores de ejemplo)
-- -----------------------------------------------------------------------------
INSERT OR IGNORE INTO sectors (id, name) VALUES
    ('550e8400-e29b-41d4-a716-446655440001', 'Producción'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Depósito'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Administración');

-- -----------------------------------------------------------------------------
-- 2) EMPLEADOS (5 empleados repartidos en sectores)
-- -----------------------------------------------------------------------------
INSERT OR IGNORE INTO employees (id, sector_id, first_name, last_name, external_code, is_active, created_at, updated_at) VALUES
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'Juan', 'Pérez', '20123456', 1, unixepoch(), unixepoch()),
    ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 'María', 'González', '22987654', 1, unixepoch(), unixepoch()),
    ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'Carlos', 'Rodríguez', NULL, 1, unixepoch(), unixepoch()),
    ('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', 'Ana', 'Martínez', '25111222', 1, unixepoch(), unixepoch()),
    ('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440003', 'Luis', 'Fernández', '27888999', 1, unixepoch(), unixepoch());

-- -----------------------------------------------------------------------------
-- 3) ADMIN USER
-- -----------------------------------------------------------------------------
-- IMPORTANTE: El password_hash 'REPLACE_ME' es un PLACEHOLDER.
-- Reemplazarlo por el hash real (bcrypt, argon2, etc.) antes de usar en producción.
--
-- Ejemplo con bcrypt (generar con: htpasswd -nbBC 10 "" tu_password | tr -d ':\n' | sed 's/$2y/$2a/')
-- O con Argon2 según tu implementación.
--
-- Buscar esta línea y reemplazar 'REPLACE_ME' por el hash real:
INSERT OR IGNORE INTO admin_users (id, username, password_hash, created_at, updated_at) VALUES
    ('770e8400-e29b-41d4-a716-446655440001', 'admin', 'REPLACE_ME', unixepoch(), unixepoch());
--
-- Para actualizar el hash después de cargado:
--   UPDATE admin_users SET password_hash = 'TU_HASH_REAL_BCRYPT_O_ARGON2' WHERE username = 'admin';
-- =============================================================================
