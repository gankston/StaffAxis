# StaffAxis API

Backend Node.js + Express + Turso (libSQL) para control de asistencia.

## Levantar desde cero (Windows)

No requiere Turso CLI ni panel web. Solo necesitas las credenciales de Turso.

### 1. Ir al directorio del servidor

```powershell
cd server
```

### 2. Instalar dependencias

```powershell
npm install
```

### 3. Completar `.env`

```powershell
copy .env.example .env
```

Editar `.env` y completar:

| Variable | Obligatoria | Descripción |
|----------|-------------|-------------|
| `TURSO_DATABASE_URL` | Sí | URL de la base Turso (ej: `libsql://staffaxis-xxx.turso.io`) |
| `TURSO_AUTH_TOKEN` | Sí | Token de autenticación Turso |
| `JWT_SECRET` | Sí | Secreto para firmar tokens JWT (ej: `node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"`) |
| `ADMIN_BOOTSTRAP_USER` | No | Usuario inicial si no existe (default: admin) |
| `ADMIN_BOOTSTRAP_PASS` | No | Contraseña del usuario inicial |
| `PORT` | No | Puerto HTTP (default: 3000) |
| `NODE_ENV` | No | `development` habilita endpoints `/dev/seed` y `/api/dev/seed-min` |

### 4. Arrancar

```powershell
npm run dev
```

Al arrancar, el servidor:

- Conecta a Turso
- Crea las tablas automáticamente si no existen (ejecuta `db/schema.sql`)
- Crea el admin bootstrap si no existe ninguno
- Expone `/health` y `/health/db`

### Obtener credenciales Turso (sin CLI)

1. Ir a [https://turso.tech](https://turso.tech) y crear cuenta
2. Crear base de datos desde el panel web
3. Copiar la URL y generar un token de acceso

## Comandos

- `npm run dev` — desarrollo con hot-reload
- `npm run build` — compila TypeScript a `dist/`
- `npm start` — ejecuta `dist/main.js`

## Endpoints de salud

- `GET /health` — estado básico
- `GET /health/db` — verifica conexión a la base de datos

## Endpoints de desarrollo (solo NODE_ENV=development)

- `POST /dev/seed` — ejecuta `db/seed.sql` completo (sectores, empleados de ejemplo). Requiere que exista el archivo.
- `POST /api/dev/seed-min` — inserta datos mínimos: 1 sector (`sector-dev-001`) y 1 empleado (`emp-dev-001`). Usa `INSERT OR IGNORE`; no duplica si ya existen. Responde con `{ sector: { id, name }, employee: { id, sector_id } }`.

## Troubleshooting

| Problema | Solución |
|----------|----------|
| `TURSO_DATABASE_URL y TURSO_AUTH_TOKEN son obligatorios` | Completar `.env` con las credenciales de Turso |
| `Bootstrap schema: archivo no encontrado` | Ejecutar desde `server/` (el path `../db/schema.sql` debe existir) |
| `Health check DB failed` | Verificar URL y token de Turso; comprobar que la DB existe |
| `npm run dev` no arranca | Revisar que `tsx` esté instalado (`npm install`) |
| Puerto 3000 en uso | Cambiar `PORT` en `.env` |

## API base

Todas las rutas están bajo `/api`.

### Códigos de error estandarizados

| Código | HTTP | Descripción |
|--------|------|-------------|
| `validation_error` | 400 | Datos inválidos (body, query o params) |
| `unauthorized` | 401 | Token faltante o inválido |
| `forbidden` | 403 | Sin permisos |
| `not_found` | 404 | Recurso no encontrado |
| `conflict` | 409 | Conflicto de estado (ej: ya aprobado) |
| `internal_error` | 500 | Error interno |

Respuesta de error: `{ "code": string, "message": string }`

---

## Ejemplos cURL

### 1. Login admin

Obtiene JWT para autenticación de admin.

```bash
curl -X POST http://localhost:3000/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"tu_password"}'
```

Respuesta:

```json
{ "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
```

### 2. Register device

Registra o actualiza un dispositivo y devuelve el token para ese device.

```bash
curl -X POST http://localhost:3000/api/auth/device/register \
  -H "Content-Type: application/json" \
  -d '{
    "device_id": "device-001",
    "sector_id": "550e8400-e29b-41d4-a716-446655440001",
    "encargado_name": "Juan Encargado"
  }'
```

Respuesta:

```json
{ "token": "sk_abc123..." }
```

**Importante:** El token se entrega una sola vez. Guardarlo para autenticación de dispositivos.

### 3. Post submission

Envía una asistencia desde el dispositivo. Requiere header `X-Device-Token`.

```bash
TOKEN="sk_abc123..."   # Token del register device

curl -X POST http://localhost:3000/api/submissions \
  -H "Content-Type: application/json" \
  -H "X-Device-Token: $TOKEN" \
  -d '{
    "employee_id": "660e8400-e29b-41d4-a716-446655440001",
    "date": "2025-02-13",
    "minutes_worked": 480,
    "check_in": "08:00",
    "check_out": "16:00",
    "notes": "Turno normal"
  }'
```

Respuesta 201:

```json
{
  "id": "uuid-...",
  "employee_id": "660e8400-...",
  "date": "2025-02-13",
  "minutes_worked": 480,
  "status": "pending",
  ...
}
```

### 4. Approve submission

Aprueba un submission pendiente y crea/actualiza el attendance. Requiere JWT de admin.

```bash
ADMIN_TOKEN="eyJhbGciOiJIUzI1NiIs..."   # Token del login admin
SUBMISSION_ID="uuid-del-submission"

curl -X POST "http://localhost:3000/api/admin/submissions/$SUBMISSION_ID/approve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "minutes_worked": 480,
    "check_in": "08:00",
    "check_out": "16:00",
    "notes": "Aprobado"
  }'
```

Respuesta:

```json
{
  "attendance": { "id": "...", "employee_id": "...", "date": "2025-02-13", ... },
  "submission": { "id": "...", "status": "approved", ... }
}
```

### 5. Pull approved (attendances por sector)

Obtiene las attendances aprobadas del sector del dispositivo desde un timestamp. Requiere `X-Device-Token`.

```bash
DEVICE_TOKEN="sk_abc123..."

curl -X GET "http://localhost:3000/api/approved?since=0" \
  -H "X-Device-Token: $DEVICE_TOKEN"
```

Respuesta:

```json
{
  "attendances": [
    {
      "id": "...",
      "employee_id": "...",
      "sector_id": "...",
      "date": "2025-02-13",
      "minutes_worked": 480,
      "check_in": "08:00",
      "check_out": "16:00",
      "updated_at": 1739452800
    }
  ]
}
```

`since` es un timestamp Unix (segundos). Solo devuelve attendances con `updated_at > since`.

---

## Tests manuales — Query params

Endpoints con query params validados (zod). Si la query es inválida, responde 400 `validation_error`.

### GET /admin/submissions

Lista paginada de submissions. Requiere JWT de admin.

| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| status | `pending` \| `approved` \| `rejected` | — | Filtrar por estado |
| sector_id | string | — | Filtrar por sector |
| date | YYYY-MM-DD | — | Filtrar por fecha |
| limit | number 1-100 | 20 | Cantidad por página |
| offset | number ≥ 0 | 0 | Desplazamiento |

```bash
ADMIN_TOKEN="eyJhbGciOiJIUzI1NiIs..."

# Todos los pendientes (default limit=20, offset=0)
curl -X GET "http://localhost:3000/api/admin/submissions" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Solo pendientes, sector específico
curl -X GET "http://localhost:3000/api/admin/submissions?status=pending&sector_id=550e8400-e29b-41d4-a716-446655440001" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Por fecha, 50 resultados
curl -X GET "http://localhost:3000/api/admin/submissions?date=2025-02-13&limit=50&offset=0" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Query inválida (date mal formateada) → 400 validation_error
curl -X GET "http://localhost:3000/api/admin/submissions?date=13-02-2025" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### GET /admin/attendances

Lista attendances aprobados. Requiere JWT de admin.

| Param | Tipo | Descripción |
|-------|------|-------------|
| sector_id | string | Filtrar por sector |
| date | YYYY-MM-DD | Filtrar por fecha |
| employee_id | string | Filtrar por empleado |

```bash
# Todos los attendances del sector
curl -X GET "http://localhost:3000/api/admin/attendances?sector_id=550e8400-e29b-41d4-a716-446655440001" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Por fecha y empleado
curl -X GET "http://localhost:3000/api/admin/attendances?date=2025-02-13&employee_id=660e8400-e29b-41d4-a716-446655440001" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### GET /approved

Attendances aprobados del sector del dispositivo. Requiere `X-Device-Token`.

| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| since | number (unix sec) | 0 | Solo `updated_at > since` |

```bash
DEVICE_TOKEN="sk_abc123..."

# Desde el inicio
curl -X GET "http://localhost:3000/api/approved?since=0" \
  -H "X-Device-Token: $DEVICE_TOKEN"

# Desde timestamp específico
curl -X GET "http://localhost:3000/api/approved?since=1739452800" \
  -H "X-Device-Token: $DEVICE_TOKEN"

# Sin since (usa default 0)
curl -X GET "http://localhost:3000/api/approved" \
  -H "X-Device-Token: $DEVICE_TOKEN"

# since inválido (negativo) → 400 validation_error
curl -X GET "http://localhost:3000/api/approved?since=-1" \
  -H "X-Device-Token: $DEVICE_TOKEN"
```

### GET /submissions/mine

Submissions del dispositivo. Requiere `X-Device-Token`.

| Param | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| since | number (unix sec) | 0 | Solo `updated_at >= since` |

```bash
DEVICE_TOKEN="sk_abc123..."

# Todas las submissions del device
curl -X GET "http://localhost:3000/api/submissions/mine" \
  -H "X-Device-Token: $DEVICE_TOKEN"

# Desde timestamp
curl -X GET "http://localhost:3000/api/submissions/mine?since=1739452800" \
  -H "X-Device-Token: $DEVICE_TOKEN"
```
