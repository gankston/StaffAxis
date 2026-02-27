# StaffAxis Server

Backend mínimo Node.js + Express + Turso.

## Estructura

```
server/
├── src/
│   ├── db/           # Cliente Turso
│   ├── middleware/   # errorHandler, validate (zod)
│   ├── routes/       # health, sectors, ...
│   ├── services/     # bootstrap admin
│   ├── utils/        # logger
│   └── main.ts
├── package.json
└── tsconfig.json
```

## Variables de entorno

| Variable | Descripción |
|----------|-------------|
| `TURSO_DATABASE_URL` | URL de la base Turso |
| `TURSO_AUTH_TOKEN` | Token de auth Turso |
| `JWT_SECRET` | Secreto para JWT admin (HS256) |
| `ADMIN_BOOTSTRAP_USER` | Usuario admin inicial |
| `ADMIN_BOOTSTRAP_PASS` | Contraseña admin inicial |
| `PORT` | Puerto (default 3000) |

Copiar `.env.example` a `.env` y completar valores.

## Scripts

```bash
npm run dev    # tsx watch (desarrollo)
npm run build  # Compilar TypeScript
npm run start  # Ejecutar dist/main.js
```

## Seguridad

- **Password:** bcrypt (hash/verify)
- **Admin:** JWT HS256 con `JWT_SECRET`, header `Authorization: Bearer <token>`
- **Device:** API token hasheado en `devices.api_token_hash`, header `X-Device-Token`

**Middleware:** `requireAdminAuth`, `requireDeviceAuth` (setea `req.adminUser` o `req.deviceAuth`)

## Endpoints

- `GET /` - Info API
- `GET /api/health` - Health check básico
- `GET /api/health/db` - Health check + consulta `SELECT 1` a Turso
- `GET /api/sectors` - Listar sectores
- `POST /api/sectors` - Crear sector (body: `{ "name": "..." }`)
- `POST /api/auth/login` - Admin login (body: `{ username, password }`) → JWT
- `GET /api/auth/me` - Admin actual (requiere JWT)
- `POST /api/auth/device/register` - Registrar dispositivo (requiere JWT admin) → token
- `GET /api/auth/device/ping` - Ping con device token (requiere X-Device-Token)
