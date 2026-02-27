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

## Endpoints

- `GET /` - Info API
- `GET /api/health` - Health check básico
- `GET /api/health/db` - Health check + consulta `SELECT 1` a Turso
- `GET /api/sectors` - Listar sectores
- `POST /api/sectors` - Crear sector (body: `{ "name": "..." }`)
