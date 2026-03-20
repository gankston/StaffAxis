# StaffAxis – Base de datos Turso

Instrucciones para configurar la base de datos central en Turso (libSQL).

---

## 1. Instalar Turso CLI

### macOS (preferido: Homebrew)
```bash
brew install tursodatabase/tap/turso
```

### macOS / Linux (alternativa)
```bash
curl -sSfL https://get.tur.so/install.sh | bash
```

### Windows
Requiere WSL. En PowerShell:
```powershell
wsl
```
Luego dentro de WSL:
```bash
curl -sSfL https://get.tur.so/install.sh | bash
```

Abrir **una nueva terminal** y verificar:
```bash
turso --version
```

---

## 2. Autenticarse
```bash
turso auth login
```
(Abre el navegador para login con GitHub)

---

## 3. Crear la base de datos
```bash
turso db create staffaxis
```

---

## 4. Obtener DATABASE_URL y AUTH_TOKEN

### DATABASE_URL
```bash
turso db show staffaxis --url
```
Copiar la URL (formato `libsql://staffaxis-...turso.io`).

### AUTH_TOKEN
```bash
turso db tokens create staffaxis
```
Copiar el token. Para producción, considerar `--expiration never` o un plazo específico.

---

## 5. Ejecutar schema.sql y seed.sql

### Opción A (preferida): redirección de archivo
Desde la raíz del repo:
```bash
turso db shell staffaxis < db/schema.sql
turso db shell staffaxis < db/seed.sql
```

### Opción B: shell interactivo con .read
```bash
turso db shell staffaxis
```
Dentro del shell:
```sql
.read db/schema.sql
.read db/seed.sql
.quit
```
(Nota: `.read` usa rutas relativas al directorio actual; asegurarse de estar en la raíz del repo.)

---

## Variables de entorno para el backend

```
DATABASE_URL=libsql://staffaxis-xxxxx-xxxxx.turso.io
AUTH_TOKEN=eyJ...
```

---

## Schema: employees.id (DNI como TEXT)

La tabla `employees` usa `id TEXT PRIMARY KEY` para almacenar DNI como string (ej: `"26401152"`). Esto está definido en `db/schema.sql` líneas 20-28.

### Verificar en Turso

```bash
turso db shell staffaxis
```

```sql
PRAGMA table_info(employees);
```

Si la columna `id` muestra `type=TEXT`, el schema es correcto. No hace falta migración.

### Migraciones

**001 - employees.id como TEXT** (solo si tu base tiene `employees.id` como INTEGER):

```bash
turso db shell staffaxis < db/migrations/001_employees_id_text.sql
```

**002 - UNIQUE index para dedup en attendance_submissions** (recomendado en prod):

Garantiza idempotencia: un solo registro por `(device_id, employee_id, date)`. Elimina duplicados existentes conservando el más antiguo antes de crear el índice.

```bash
turso db shell staffaxis < db/migrations/002_attendance_submissions_dedup_unique.sql
```

O desde shell interactivo:

```sql
.read db/migrations/001_employees_id_text.sql
.read db/migrations/002_attendance_submissions_dedup_unique.sql
```
