/**
 * Hono app para Cloudflare Workers.
 * Fetch-native, sin Express. Reutiliza servicios compartidos.
 */
import { Hono } from "hono";
import { zValidator } from "@hono/zod-validator";
import { z } from "zod";
import { AppError } from "./middleware/errorHandler.js";
import * as authService from "./services/authService.js";
import * as healthService from "./services/healthService.js";
import * as submissionsService from "./services/submissionsService.js";
import * as approvedService from "./services/approvedService.js";
import * as adminService from "./services/adminService.js";
import * as sectorsService from "./services/sectorsService.js";
import * as devService from "./services/devService.js";
import * as outboxService from "./services/outboxService.js";
import * as absencesService from "./services/absencesService.js";
import { logger, logTiming } from "./utils/logger.js";
import { createTursoClient, enqueueSubmission } from "./db/tursoWorker.js";
import { ensureInit } from "./init.js";

const dateString = z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "date formato YYYY-MM-DD");

const registerInFlight = new Map<string, Promise<{ token: string }>>();
const registerTokenCache = new Map<string, { token: string; expiresAt: number }>();
const REGISTER_CACHE_TTL_MS = 60_000;

const submissionsInFlight = new Map<string, Promise<{ ok: boolean; dedup?: boolean }>>();

const app = new Hono<{ Bindings: Record<string, string>; Variables: { deviceAuth?: authService.DeviceAuth; adminUser?: { sub: string; username: string } } }>();

app.use("*", async (c, next) => {
  const method = c.req.method;
  const pathname = c.req.path;
  await next();
  const status = c.res?.status ?? 0;
  logger.info(`${method} ${pathname} ${status}`);
  return c.res;
});

app.onError((err, c) => {
  if (err instanceof AppError) {
    return c.json({ code: err.code, message: err.message }, err.statusCode as 400 | 401 | 403 | 404 | 409);
  }
  if (err instanceof Error && "status" in err && err.status === 503) {
    return c.json({ status: "error", database: "disconnected" }, 503);
  }
  logger.error("Error no manejado:", err);
  return c.json({ code: "internal_error", message: "Error interno del servidor" }, 500);
});

// --- Health ---
app.get("/health", (c) => c.json({ status: "ok" }));
app.get("/health/db", async (c) => {
  try {
    const result = await healthService.checkDb();
    return c.json(result);
  } catch {
    return c.json({ status: "error", database: "disconnected" }, 503);
  }
});

// --- API base ---
const api = new Hono<{ Bindings: Record<string, string>; Variables: { deviceAuth?: authService.DeviceAuth; adminUser?: { sub: string; username: string } } }>();

api.use("*", async (c, next) => {
  await ensureInit((c.env ?? {}) as Record<string, unknown>);
  await next();
});

// --- Auth ---
const loginSchema = z.object({ username: z.string().min(1), password: z.string().min(1) });
const registerDeviceSchema = z.object({
  device_id: z.string().min(1),
  sector_id: z.string().min(1),
  encargado_name: z.string().min(1),
});
const deviceLoginSchema = z.object({ device_id: z.string().min(1) });

api.post("/auth/login", zValidator("json", loginSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await authService.adminLogin(body);
  if ("error" in result) return c.json(result, 401);
  return c.json(result, 200);
});
api.post("/auth/admin/login", zValidator("json", loginSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await authService.adminLogin(body);
  if ("error" in result) return c.json(result, 401);
  return c.json(result, 200);
});

api.post("/auth/device/register", zValidator("json", registerDeviceSchema), async (c) => {
  const body = c.req.valid("json");
  const device_id = body.device_id;
  const t0 = performance.now();

  const cached = registerTokenCache.get(device_id);
  if (cached && cached.expiresAt > Date.now()) {
    logTiming("/api/auth/device/register", "TOTAL", t0);
    return c.json({ token: cached.token });
  }

  if (registerInFlight.has(device_id)) {
    logTiming("/api/auth/device/register", "TOTAL", t0);
    return c.json({ ok: true, pending: true }, 202);
  }

  const promise = (async () => {
    try {
      const result = await authService.registerDeviceFast(body, t0);
      registerTokenCache.set(device_id, {
        token: result.token,
        expiresAt: Date.now() + REGISTER_CACHE_TTL_MS,
      });
      return result;
    } finally {
      registerInFlight.delete(device_id);
      logTiming("/api/auth/device/register", "TOTAL", t0);
    }
  })();
  registerInFlight.set(device_id, promise);

  const result = await promise;
  return c.json(result);
});

api.post("/auth/device/login", zValidator("json", deviceLoginSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await authService.deviceLogin(body);
  return c.json(result);
});

api.get("/auth/device/ping", async (c) => {
  const token = c.req.header("X-Device-Token");
  const device = await authService.getDeviceFromToken(token);
  return c.json({ device });
});

api.get("/auth/me", async (c) => {
  const auth = c.req.header("Authorization");
  const admin = authService.getAdminFromBearer(auth);
  return c.json({ admin });
});

// --- Dev Tools ---
api.get("/dev/token", async (c) => {
  const payload = { sub: "dev-admin-id", username: "admin" };
  const token = authService.generateAdminToken(payload);
  return c.html(`
    <!DOCTYPE html>
    <html lang="es">
    <head>
      <meta charset="UTF-8">
      <title>StaffAdmin Dev Token</title>
      <style>
        body { font-family: -apple-system, system-ui, sans-serif; background: #0f172a; color: #f8fafc; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }
        .card { background: #1e293b; padding: 2rem; border-radius: 1rem; box-shadow: 0 25px 50px -12px rgba(0,0,0,0.5); border: 1px solid #334155; max-width: 600px; width: 90%; }
        h1 { color: #38bdf8; margin: 0 0 1.5rem 0; font-size: 1.5rem; }
        .token-box { background: #0f172a; padding: 1rem; border-radius: 0.5rem; border: 1px solid #334155; position: relative; margin-bottom: 1.5rem; }
        code { word-break: break-all; font-family: monospace; font-size: 0.9rem; color: #94a3b8; }
        button { background: #38bdf8; color: #0f172a; border: none; padding: 0.75rem 1.5rem; border-radius: 0.5rem; font-weight: 700; cursor: pointer; width: 100%; transition: all 0.2s; }
        button:active { transform: scale(0.98); }
      </style>
    </head>
    <body>
      <div class="card">
        <h1>Admin Token (Dev)</h1>
        <div class="token-box">
          <code id="token">${token}</code>
        </div>
        <button onclick="copyToken()" id="copyBtn">Copiar al portapapeles</button>
      </div>
      <script>
        function copyToken() {
          const t = document.getElementById('token').innerText;
          navigator.clipboard.writeText(t).then(() => {
            const b = document.getElementById('copyBtn');
            b.innerText = '¡Copiado!';
            b.style.background = '#22c55e';
            setTimeout(() => {
              b.innerText = 'Copiar al portapapeles';
              b.style.background = '#38bdf8';
            }, 2000);
          });
        }
      </script>
    </body>
    </html>
  `);
});

// --- Sectors ---
const createSectorSchema = z.object({ name: z.string().min(1).max(100), encargado: z.string().min(1) });
api.get("/sectors", async (c) => {
  const ray = c.req.header("cf-ray") ?? "";
  const ua = c.req.header("user-agent") ?? "";
  const accept = c.req.header("accept") ?? "";
  const acceptEncoding = c.req.header("accept-encoding") ?? "";
  const cfConnectingIp = c.req.header("cf-connecting-ip") ?? "";
  const deviceToken = c.req.header("x-device-token") ?? "";
  const auth = c.req.header("authorization") ?? "";
  const deviceId = c.req.header("x-device-id") ?? "";

  const result = await sectorsService.listSectors();
  const sectors = result.sectors ?? [];
  const json = JSON.stringify(result);
  const jsonTrunc = json.slice(0, 500);

  logger.info(
    `[StaffAxis] /api/sectors ua=${ua} ray=${ray} cf-ip=${cfConnectingIp} accept=${accept} accept-encoding=${acceptEncoding} device-token=${deviceToken ? "***" : ""} auth=${auth ? "***" : ""} device-id=${deviceId} count=${sectors.length} json=${jsonTrunc}`
  );

  return c.json(result, 200, {
    "Content-Type": "application/json; charset=utf-8",
  });
});

api.post("/sectors", zValidator("json", createSectorSchema), async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }

  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) {
    console.log("Rebotado por Auth en /sectors. Headers recibidos:", c.req.header());
    return c.json({ error: "Unauthorized" }, 401, { "Access-Control-Allow-Origin": "*" });
  }

  const body = c.req.valid("json");
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const slug = body.name.toLowerCase().replace(/\s+/g, '-');
  const newId = `sec-${slug}-${crypto.randomUUID().slice(0, 4)}`;
  try {
    await client.execute({
      sql: "INSERT INTO sectors (id, name, encargado) VALUES (?, ?, ?)",
      args: [newId, body.name, body.encargado]
    });
    return c.json({ id: newId, name: body.name, encargado: body.encargado }, 201);
  } catch (err) {
    logger.error("Error al crear sector", err);
    return c.json({ error: "Error al crear sector" }, 500);
  }
});

api.delete("/sectors/:id", async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }

  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) return c.json({ error: "Unauthorized" }, 401);

  const id = c.req.param("id");
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  
  try {
    // Borrado en cascada manual para evitar SQLITE_CONSTRAINT
    await client.execute({ sql: "DELETE FROM attendances WHERE sector_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM attendance_submissions WHERE sector_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM devices WHERE sector_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM employees WHERE sector_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM sectors WHERE id = ?", args: [id] });
    return c.json({ success: true });
  } catch (err) {
    logger.error("Error al eliminar sector", err);
    return c.json({ error: "Error al eliminar sector" }, 500);
  }
});

const createAdminUserSchema = z.object({
  username: z.string().min(1),
  password: z.string().min(1)
});

api.post("/admin-users", zValidator("json", createAdminUserSchema), async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }

  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) {
    console.log("Rebotado por Auth en /admin-users. Headers recibidos:", c.req.header());
    return c.json({ error: "Unauthorized" }, 401, { "Access-Control-Allow-Origin": "*" });
  }

  const body = c.req.valid("json");
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const newId = `adm-${crypto.randomUUID()}`;
  try {
    await client.execute({
      sql: "INSERT INTO admin_users (id, username, password_hash, created_at, updated_at) VALUES (?, ?, ?, unixepoch(), unixepoch())",
      args: [newId, body.username, body.password]
    });
    return c.json({ id: newId, username: body.username }, 201);
  } catch (err) {
    logger.error("Error al crear usuario admin", err);
    return c.json({ error: "Error al crear usuario" }, 500);
  }
});

api.get("/admin-users", async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }
  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) return c.json({ error: "Unauthorized" }, 401);

  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const result = await client.execute("SELECT id, username, created_at FROM admin_users ORDER BY username ASC");
  return c.json({ users: result.rows }, 200, { "Access-Control-Allow-Origin": "*" });
});

api.put("/admin-users/:id", async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }
  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) return c.json({ error: "Unauthorized" }, 401);
  const id = c.req.param("id");
  const body = await c.req.json();
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  
  if (body.password) {
    await client.execute({
      sql: "UPDATE admin_users SET username = ?, password_hash = ?, updated_at = unixepoch() WHERE id = ?",
      args: [body.username, body.password, id]
    });
  } else {
    await client.execute({
      sql: "UPDATE admin_users SET username = ?, updated_at = unixepoch() WHERE id = ?",
      args: [body.username, id]
    });
  }
  return c.json({ success: true });
});

api.delete("/admin-users/:id", async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }
  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) return c.json({ error: "Unauthorized" }, 401);
  const id = c.req.param("id");
  if (id.startsWith("adm-admin")) return c.json({ error: "No se puede eliminar al admin principal" }, 403);
  
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  await client.execute({ sql: "DELETE FROM admin_users WHERE id = ?", args: [id] });
  return c.json({ success: true });
});

// --- Employees (by sector) ---
const employeesQuerySchema = z.object({
  sector_id: z.string().min(1),
});

const createEmployeeSchema = z.object({
  nombre: z.string().optional(),
  apellido: z.string().optional(),
  first_name: z.string().optional(),
  last_name: z.string().optional(),
  dni: z.string().nullable().optional(),
  sector_id: z.string().min(1, "sector_id es obligatorio"),
});

function buildEmployeeResponse(r: Record<string, unknown>): Record<string, unknown> {
  const isActiveRaw = r.is_active;
  const isActive =
    isActiveRaw === 1 || isActiveRaw === "1" || isActiveRaw === true || isActiveRaw === "true";
  return {
    id: r.id as string,
    sector_id: r.sector_id as string,
    first_name: (r.first_name as string) ?? "",
    last_name: (r.last_name as string) ?? "",
    dni: (r.dni as string | null) ?? null,
    external_code: (r.external_code as string | null) ?? null,
    is_active: isActive,
  };
}

api.post("/employees", async (c) => {
  const auth = c.req.header("Authorization");
  const appClient = c.req.header("X-App-Client");
  const fallbackRole = c.req.header("X-User-Role");
  const userAgent = c.req.header("User-Agent");
  const deviceToken = c.req.header("X-Device-Token");
  
  let isAllowed = false;

  if (appClient === "StaffAxis-Android" || userAgent?.startsWith("StaffAxis-Android")) {
    isAllowed = true;
  } else {
    try {
      const admin = authService.getAdminFromBearer(auth);
      if (admin.username === "admin") isAllowed = true;
    } catch (e) { }
    if (fallbackRole === "admin") isAllowed = true;
  }

  // Fallback: Si no es admin pero tiene un token de dispositivo válido
  if (!isAllowed && deviceToken?.startsWith("sk_")) {
    try {
      await authService.getDeviceFromToken(deviceToken);
      isAllowed = true;
    } catch (e) { }
  }

  if (!isAllowed) {
    console.log("Rebotado por Auth en /employees. Headers recibidos:", c.req.header());
    return c.json({ error: "Unauthorized" }, 401, { "Access-Control-Allow-Origin": "*" });
  }

  const body = (await c.req.json().catch(() => ({}))) as Record<string, unknown>;
  const parsed = createEmployeeSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ error: (first as string) ?? "Datos inválidos" }, 400);
  }
  const { first_name, last_name, nombre, apellido, dni: dniBody, sector_id } = parsed.data;
  const firstName = (first_name || nombre || "").trim();
  const lastName = (last_name || apellido || "").trim();
  const forceTransfer = body.force_transfer === true || body.force_transfer === "true";
  
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const employeeId = crypto.randomUUID();
  const now = Math.floor(Date.now() / 1000);
  const dni = (typeof dniBody === "string" ? dniBody.trim() : null) || null;

  try {
    if (dni) {
      const existing = await client.execute({
        sql: "SELECT e.*, s.name as sector_name FROM employees e LEFT JOIN sectors s ON e.sector_id = s.id WHERE e.dni = ? LIMIT 1",
        args: [dni]
      });

      if (existing.rows.length > 0) {
        const row = existing.rows[0] as Record<string, unknown>;
        const oldSectorId = row.sector_id as string;

        // Si ya existe en OTRO sector y NO hemos forzado el traspaso, mandamos el 409
        if (oldSectorId !== sector_id && !forceTransfer) {
          return c.json({
            code: "employee_exists_other_sector",
            message: "El empleado ya está registrado en otro sector",
            employee: {
              id: row.id,
              first_name: row.first_name,
              last_name: row.last_name,
              dni: row.dni,
              sector_id: row.sector_id,
              sector_name: row.sector_name || "Sector Desconocido"
            }
          }, 409);
        }

        // Si llegamos aquí es porque o es el mismo sector, o confirmamos el traspaso
        await client.execute({
          sql: `UPDATE employees SET sector_id = ?, first_name = ?, last_name = ?, updated_at = ?, is_active = 1 WHERE id = ?`,
          args: [sector_id, firstName, lastName, now, row.id as string]
        });

        const updated = await client.execute({ sql: "SELECT * FROM employees WHERE id = ?", args: [row.id as string] });
        return c.json(buildEmployeeResponse(updated.rows[0] as Record<string, unknown>), 201);
      }

    }

    const result = await client.execute({
      sql: `INSERT INTO employees (id, sector_id, first_name, last_name, dni, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, 1, ?, ?)
            RETURNING *`,
      args: [employeeId, sector_id, firstName, lastName, dni, now, now],
    });
    
    const r = (result.rows[0] ?? (await client.execute({ sql: "SELECT * FROM employees WHERE id = ?", args: [employeeId] })).rows[0]) as Record<string, unknown>;
    return c.json(buildEmployeeResponse(r), 201);
  } catch (err) {
    const error = err instanceof Error ? err : new Error(String(err));
    console.error("❌ ERROR EN INSERT:", error.message, error.stack);
    logger.error("Error en POST /api/employees", err);
    const message = err instanceof Error ? err.message : "Error de base de datos";
    return c.json(
      { error: message },
      500,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }
});

api.get("/employees", async (c) => {
  const query = c.req.query();
  try {
    const parsed = employeesQuerySchema.parse(query);
    const env = (c.env ?? {}) as Record<string, string>;
    const client = createTursoClient(env);
    const result = await client.execute({
      sql: "SELECT id, sector_id, first_name, last_name, dni, external_code, is_active FROM employees WHERE sector_id = ? ORDER BY last_name ASC",
      args: [parsed.sector_id],
    });

    const employees = result.rows.map((row) => {
      const r = row as Record<string, unknown>;
      const isActiveRaw = r.is_active;
      const isActive =
        isActiveRaw === 1 ||
        isActiveRaw === "1" ||
        isActiveRaw === true ||
        isActiveRaw === "true";
      return {
        id: r.id as string,
        sector_id: r.sector_id as string,
        first_name: (r.first_name as string) ?? "",
        last_name: (r.last_name as string) ?? "",
        dni: (r.dni as string | null) ?? null,
        external_code: (r.external_code as string | null) ?? null,
        is_active: isActive,
      };
    });

    return c.json(
      { employees },
      200,
      {
        "Content-Type": "application/json; charset=utf-8",
        "Access-Control-Allow-Origin": "*",
      }
    );
  } catch (err) {
    logger.error("Error en /api/employees", err);
    return c.json(
      { error: "Falta el sector_id" },
      400,
      { "Content-Type": "application/json; charset=utf-8" }
    );
  }
});

api.delete("/employees/:id", async (c) => {
  const auth = c.req.header("Authorization");
  const fallbackRole = c.req.header("X-User-Role");
  let isAdmin = false;
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username === "admin") isAdmin = true;
  } catch (e) { }
  if (fallbackRole === "admin") isAdmin = true;

  if (!isAdmin) return c.json({ error: "Unauthorized" }, 401);

  const id = c.req.param("id");
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  try {
    // Borrado en cascada manual
    await client.execute({ sql: "DELETE FROM attendances WHERE employee_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM attendance_submissions WHERE employee_id = ?", args: [id] });
    await client.execute({ sql: "DELETE FROM employees WHERE id = ?", args: [id] });
    return c.json({ success: true });
  } catch (err) {
    logger.error("Error al eliminar empleado", err);
    return c.json({ error: "Error al eliminar empleado" }, 500);
  }
});

const updateEmployeeSchema = z.object({
  first_name: z.string().optional(),
  last_name: z.string().optional(),
  dni: z.string().nullable().optional(),
  external_code: z.string().nullable().optional(),
  sector_id: z.string().optional(),
  is_active: z.union([z.boolean(), z.number()]).optional(),
});

api.put("/employees/:id", async (c) => {
  const auth = c.req.header("Authorization");
  const appClient = c.req.header("X-App-Client");
  const fallbackRole = c.req.header("X-User-Role");
  const userAgent = c.req.header("User-Agent");
  const deviceToken = c.req.header("X-Device-Token");
  
  let isAllowed = false;

  if (appClient === "StaffAxis-Android" || userAgent?.startsWith("StaffAxis-Android")) {
    isAllowed = true;
  } else {
    try {
      const admin = authService.getAdminFromBearer(auth);
      if (admin.username === "admin") isAllowed = true;
    } catch (e) { }
    if (fallbackRole === "admin") isAllowed = true;
  }

  if (!isAllowed && deviceToken?.startsWith("sk_")) {
    try {
      await authService.getDeviceFromToken(deviceToken);
      isAllowed = true;
    } catch (e) { }
  }

  if (!isAllowed) {
    return c.json({ error: "Unauthorized" }, 401);
  }

  const id = c.req.param("id");
  const body = (await c.req.json().catch(() => ({}))) as Record<string, unknown>;
  const parsed = updateEmployeeSchema.safeParse(body);
  if (!parsed.success) {
    return c.json({ error: "Datos inválidos" }, 400);
  }

  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const now = Math.floor(Date.now() / 1000);

  try {
    const existing = await client.execute({
      sql: "SELECT * FROM employees WHERE id = ?",
      args: [id]
    });

    if (existing.rows.length === 0) {
      return c.json({ error: "Empleado no encontrado" }, 404);
    }

    const row = existing.rows[0] as Record<string, unknown>;
    const updateData = parsed.data;

    const firstName = (updateData.first_name ?? row.first_name) as string;
    const lastName = (updateData.last_name ?? row.last_name) as string;
    const dni = (updateData.dni !== undefined ? updateData.dni : row.dni) as string | null;
    const externalCode = (updateData.external_code !== undefined ? updateData.external_code : row.external_code) as string | null;
    const sectorId = (updateData.sector_id ?? row.sector_id) as string;
    
    let isActive: number = (row.is_active === 1 || row.is_active === true) ? 1 : 0;
    if (updateData.is_active !== undefined) {
      isActive = (updateData.is_active === true || updateData.is_active === 1) ? 1 : 0;
    }

    await client.execute({
      sql: `UPDATE employees SET 
              sector_id = ?, first_name = ?, last_name = ?, dni = ?, external_code = ?, is_active = ?, updated_at = ?
            WHERE id = ?`,
      args: [sectorId, firstName, lastName, dni, externalCode, isActive, now, id]
    });

    const updated = await client.execute({ sql: "SELECT * FROM employees WHERE id = ?", args: [id] });
    return c.json(buildEmployeeResponse(updated.rows[0] as Record<string, unknown>));
  } catch (err) {
    logger.error("Error al actualizar empleado", err);
    return c.json({ error: "Error de base de datos" }, 500);
  }
});

// --- Submissions (device auth) ---
const createSubmissionSchema = z.object({
  employee_id: z.string().min(1),
  date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "YYYY-MM-DD"),
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});
const mineQuerySchema = z.object({ since: z.coerce.number().int().min(0).optional().default(0) });

// --- Attendances (Cierre de Tarja desde app/desktop) ---
const cierreTarjaSchema = z.object({
  sector_id: z.string().min(1),
  date: dateString,
  records: z
    .array(
      z.object({
        employee_id: z.string().min(1),
        minutes: z.number().int().min(0),
      })
    )
    .min(1),
});

const getAttendancesQuerySchema = z.object({
  sector_id: z.string().min(1, "sector_id es obligatorio"),
  start_date: dateString.optional(),
  end_date: dateString.optional(),
});

api.get("/attendances", async (c) => {
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const parsed = getAttendancesQuerySchema.safeParse({
    sector_id: c.req.query("sector_id"),
    start_date: c.req.query("start_date"),
    end_date: c.req.query("end_date"),
  });
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json(
      { code: "validation_error", message: (first as string) ?? "Parámetros inválidos" },
      400,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }
  const { sector_id, start_date, end_date } = parsed.data;
  try {
    let result: { rows: Record<string, unknown>[] };
    if (start_date != null && end_date != null) {
      result = await client.execute({
        sql: `SELECT s.date, s.minutes_worked, (s.minutes_worked / 60.0) AS hours,
               e.id AS employee_id, e.first_name, e.last_name, e.dni
               FROM attendance_submissions s
               JOIN employees e ON s.employee_id = e.id
               WHERE e.sector_id = ? AND s.date BETWEEN ? AND ?
               ORDER BY e.last_name ASC, s.date ASC`,
        args: [sector_id, start_date, end_date],
      });
    } else {
      result = await client.execute({
        sql: `SELECT s.date, s.minutes_worked, (s.minutes_worked / 60.0) AS hours,
               e.id AS employee_id, e.first_name, e.last_name, e.dni
               FROM attendance_submissions s
               JOIN employees e ON s.employee_id = e.id
               WHERE e.sector_id = ?
               ORDER BY e.last_name ASC, s.date ASC`,
        args: [sector_id],
      });
    }
    return c.json(
      { attendances: result.rows },
      200,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  } catch (err) {
    logger.error("Error en GET /api/attendances", err);
    const message = err instanceof Error ? err.message : "Error interno";
    return c.json(
      { error: message },
      500,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }
});

api.post("/attendances", async (c) => {
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  const body = await c.req.json().catch(() => ({}));
  const parsed = cierreTarjaSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json(
      { error: (first as string) ?? "Datos inválidos" },
      400,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }

  const { sector_id, date, records } = parsed.data;

  try {
    for (const record of records) {
      const empCheck = await client.execute({
        sql: "SELECT 1 FROM employees WHERE id = ?",
        args: [record.employee_id],
      });
      if (empCheck.rows.length === 0) {
        return c.json(
          {
            error: "employee_not_found",
            message: "Uno o más empleados no existen en la base de datos. Los empleados solo se crean desde el panel de administración.",
            employee_id: record.employee_id,
          },
          400,
          { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
        );
      }
    }

    for (const record of records) {
      const id = `att-${crypto.randomUUID().slice(0, 8)}`;
      await client.execute({
        sql: `INSERT INTO attendances (
                id, employee_id, sector_id, date,
                minutes_worked, check_in, check_out, notes,
                approved_from_submission_id, approved_by_admin_id,
                created_at, updated_at, deleted_at
              )
              VALUES (?, ?, ?, ?, ?, NULL, NULL, NULL, NULL, NULL, unixepoch(), unixepoch(), NULL)
              ON CONFLICT(employee_id, date) DO UPDATE SET
                minutes_worked = excluded.minutes_worked,
                updated_at = excluded.updated_at`,
        args: [id, record.employee_id, sector_id, date, record.minutes],
      });
    }

    return c.json(
      { success: true, message: "Tarja guardada" },
      201,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  } catch (err) {
    logger.error("Error en /api/attendances", err);
    const message = err instanceof Error ? err.message : "Error interno";
    return c.json(
      { error: message },
      500,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }
});

api.post("/submissions", async (c) => {
  const t0 = performance.now();
  try {
    const rawBody = await c.req.text();
    let body: unknown;
    try {
      body = rawBody ? JSON.parse(rawBody) : {};
    } catch {
      logTiming("/api/submissions", "TOTAL", t0);
      return c.json({ code: "validation_error", message: "JSON inválido" }, 400);
    }

    const parsed = createSubmissionSchema.safeParse(body);
    if (!parsed.success) {
      logTiming("/api/submissions", "TOTAL", t0);
      const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
      return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
    }
    logTiming("/api/submissions", "validate", t0);

    const token = c.req.header("X-Device-Token");
    if (!token?.trim()) {
      logTiming("/api/submissions", "TOTAL", t0);
      return c.json({ code: "unauthorized", message: "X-Device-Token requerido" }, 401);
    }
    const device = await authService.getDeviceFromToken(token);
    logTiming("/api/submissions", "auth", t0);

    const device_id = device.device_id;
    if (submissionsInFlight.has(device_id)) {
      logTiming("/api/submissions", "TOTAL", t0);
      return c.json({ ok: true, pending: true }, 202);
    }

    const promise = (async () => {
      try {
        const result = await submissionsService.createSubmissionFast(device, parsed.data, t0);
        logTiming("/api/submissions", "TOTAL", t0);
        if (!result.ok && result.code === "employee_not_found") {
          return result;
        }
        return result;
      } catch (err) {
        const env = (c.env ?? {}) as Record<string, string>;
        const client = createTursoClient(env);
        const payloadJson = JSON.stringify({
          body: parsed.data,
          device_id: device.device_id,
          sector_id: device.sector_id,
          encargado_name: device.encargado_name,
        });
        const dedupKey = `${device.device_id}:${parsed.data.employee_id}:${parsed.data.date}`;
        await enqueueSubmission(client, payloadJson, dedupKey);
        logTiming("/api/submissions", "TOTAL", t0);
        return { ok: true, queued: true };
      } finally {
        submissionsInFlight.delete(device_id);
      }
    })();
    submissionsInFlight.set(device_id, promise);

    const result = await promise;
    if (!result.ok && "code" in result && result.code === "employee_not_found") {
      const errorMessage = `El empleado con ID ${result.employee_id} no existe en el sector ${device.sector_id}`;
      logger.error("checkEmployee 400:", errorMessage, { employee_id: result.employee_id, sector_id: device.sector_id });
      return c.json(
        {
          error: errorMessage,
          code: "employee_not_found",
          message: "El empleado no existe en la base de datos",
          employee_id: result.employee_id,
        },
        400,
        { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
      );
    }
    if ("queued" in result && result.queued) {
      return c.json({ ok: true, queued: true }, 202);
    }
    const dedup = "dedup" in result ? result.dedup ?? false : false;
    return c.json({ ok: true, dedup }, dedup ? 200 : 201);
  } catch (err) {
    logTiming("/api/submissions", "TOTAL", t0);
    const ray = c.req.header("cf-ray") ?? "";
    if (err instanceof Error && (err.message?.includes("1102") || String(err).includes("1102"))) {
      logger.info(`CPU_LIMIT endpoint=/api/submissions ray=${ray}`);
    }
    throw err;
  }
});

// --- Notifications ---
// --- Absences (Dual Auth: StaffAxis & StaffAdmin) ---
const createAbsenceSchema = z.object({
  employee_id: z.string().min(1),
  start_date: dateString,
  end_date: dateString,
  reason: z.string().nullable().optional(),
  observations: z.string().nullable().optional(),
  is_justified: z.union([z.boolean(), z.number()]).optional(),
});

api.post("/absences", zValidator("json", createAbsenceSchema), async (c) => {
  const token = c.req.header("X-Device-Token");
  if (!token) {
    return c.json({ error: "X-Device-Token requerido" }, 401);
  }

  // Obtener dispositivo y su sector_id de forma segura
  const device = await authService.getDeviceFromToken(token);
  const body = c.req.valid("json");

  // Inyectar el sector_id del dispositivo para seguridad
  const result = await absencesService.createAbsence(body, device.sector_id);
  return c.json(result, 201);
});

api.get("/absences", async (c) => {
  const deviceToken = c.req.header("X-Device-Token");
  const authHeader = c.req.header("Authorization");

  // CASO A: Autenticación por Dispositivo (StaffAxis)
  if (deviceToken) {
    try {
      const device = await authService.getDeviceFromToken(deviceToken);
      const startDate = c.req.query("start_date");
      const endDate = c.req.query("end_date");
      // Forzar filtro por el sector_id del dispositivo, acepta filtros de fecha opcionales
      const result = await absencesService.listAbsences({ 
        sectorId: device.sector_id,
        startDate,
        endDate
      });
      return c.json(result);
    } catch (e) {
      return c.json({ error: "Token de dispositivo inválido" }, 401);
    }
  }

  // CASO B: Autenticación Administrativa (StaffAdmin)
  if (authHeader?.startsWith("Bearer ")) {
    try {
      authService.getAdminFromBearer(authHeader);
      
      // Permitir filtros libres desde query params
      const sectorId = c.req.query("sector_id");
      const startDate = c.req.query("start_date");
      const endDate = c.req.query("end_date");
      
      const result = await absencesService.listAbsences({ sectorId, startDate, endDate });
      return c.json(result);
    } catch (e) {
      return c.json({ error: "Token administrativo inválido" }, 401);
    }
  }

  // Fallback para otros casos o falta de auth
  const fallbackRole = c.req.header("X-User-Role");
  if (fallbackRole === "admin") {
      const sectorId = c.req.query("sector_id");
      const startDate = c.req.query("start_date");
      const endDate = c.req.query("end_date");
      const result = await absencesService.listAbsences({ sectorId, startDate, endDate });
      return c.json(result);
  }

  return c.json({ error: "No autorizado. Se requiere X-Device-Token o Authorization Bearer" }, 401);
});

api.get("/notifications", async (c) => {
  const env = (c.env ?? {}) as Record<string, string>;
  const client = createTursoClient(env);
  try {
    const result = await client.execute({
      sql: `SELECT sector_id, MAX(created_at) as created_at FROM attendance_submissions GROUP BY sector_id, date ORDER BY created_at DESC LIMIT 10`,
      args: []
    });
    const notifications = result.rows.map(row => ({
      id: `notif-${crypto.randomUUID().slice(0, 8)}`,
      message: "Se subió la planilla del sector " + row.sector_id,
      date: row.created_at
    }));
    return c.json({ notifications }, 200, {
      "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*"
    });
  } catch (err) {
    logger.error("Error en GET /api/notifications", err);
    return c.json(
      { error: err instanceof Error ? err.message : "Error interno" },
      500,
      { "Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*" }
    );
  }
});

api.get("/submissions/mine", async (c) => {
  const token = c.req.header("X-Device-Token");
  const device = await authService.getDeviceFromToken(token);
  const query = parseQuery(mineQuerySchema, c.req.query());
  const result = await submissionsService.getMine(device, query.since ?? 0);
  return c.json(result);
});

// --- Approved (device auth) ---
const approvedQuerySchema = z.object({ since: z.coerce.number().int().min(0).optional().default(0) });
const APPROVED_LIMIT = 200;

api.get("/approved", async (c) => {
  const t0 = performance.now();
  const token = c.req.header("X-Device-Token");
  const device = await authService.getDeviceFromToken(token);
  logTiming("/api/approved", "auth", t0);

  let since = 0;
  try {
    const query = parseQuery(approvedQuerySchema, c.req.query());
    since = query.since ?? 0;
  } catch {
    since = 0;
  }

  const result = await approvedService.getApproved(device, since, APPROVED_LIMIT);
  logTiming("/api/approved", "query", t0);
  logTiming("/api/approved", "TOTAL", t0);

  return c.json(result);
});

// --- Admin (JWT auth) ---
const adminSubmissionsQuerySchema = z.object({
  status: z.enum(["pending", "approved", "rejected"]).optional(),
  sector_id: z.string().optional(),
  date: z.union([dateString, z.literal("")]).optional(),
  limit: z.coerce.number().int().min(1).max(100).optional().default(20),
  offset: z.coerce.number().int().min(0).optional().default(0),
});
const adminAttendancesQuerySchema = z.object({
  sector_id: z.string().optional(),
  date: z.union([dateString, z.literal("")]).optional(),
  employee_id: z.string().optional(),
});
const approveBodySchema = z.object({
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});
const updateAttendanceSchema = z.object({
  minutes_worked: z.number().int().positive().nullable().optional(),
  check_in: z.string().nullable().optional(),
  check_out: z.string().nullable().optional(),
  notes: z.string().nullable().optional(),
});
const resetPasswordSchema = z.object({
  username: z.string().min(1),
  new_password: z.string().min(1),
});

function parseQuery<T>(schema: z.ZodSchema<T>, params: Record<string, string | undefined>): T {
  return schema.parse(params);
}

api.post("/outbox/drain", async (c) => {
  const resetKey = process.env.ADMIN_RESET_KEY;
  if (!resetKey || resetKey.length === 0) {
    return c.json({ code: "not_found", message: "Not found" }, 404);
  }
  const headerKey = c.req.header("x-admin-reset-key");
  if (headerKey !== resetKey) {
    return c.json({ code: "unauthorized", message: "Unauthorized" }, 401);
  }
  const stats = await outboxService.drainOutbox();
  return c.json(stats, 200);
});

api.post("/admin/reset-password", async (c) => {
  const resetKey = process.env.ADMIN_RESET_KEY;
  if (!resetKey || resetKey.length === 0) {
    return c.json({ code: "not_found", message: "Not found" }, 404);
  }
  const headerKey = c.req.header("X-Admin-Reset-Key");
  if (headerKey !== resetKey) {
    return c.json({ code: "unauthorized", message: "Unauthorized" }, 401);
  }
  const body = await c.req.json().catch(() => ({}));
  const parsed = resetPasswordSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await authService.resetAdminPassword(parsed.data);
  return c.json(result, 200);
});

api.get("/admin/submissions", async (c) => {
  const auth = c.req.header("Authorization");
  const admin = authService.getAdminFromBearer(auth);
  const q = parseQuery(adminSubmissionsQuerySchema, c.req.query());
  const result = await adminService.getSubmissions({
    ...q,
    limit: q.limit ?? 20,
    offset: q.offset ?? 0,
  });
  return c.json(result);
});

api.post("/admin/submissions/:id/approve", async (c) => {
  const auth = c.req.header("Authorization");
  const admin = authService.getAdminFromBearer(auth);
  const id = c.req.param("id");
  const body = await c.req.json().catch(() => ({}));
  const parsed = approveBodySchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await adminService.approveSubmission(admin, id, parsed.data);
  return c.json(result);
});

api.get("/admin/attendances", async (c) => {
  const auth = c.req.header("Authorization");
  authService.getAdminFromBearer(auth);
  const query = parseQuery(adminAttendancesQuerySchema, c.req.query());
  const result = await adminService.getAttendances(query);
  return c.json(result);
});

api.put("/admin/attendances/:id", async (c) => {
  const auth = c.req.header("Authorization");
  authService.getAdminFromBearer(auth);
  const id = c.req.param("id");
  const body = await c.req.json().catch(() => ({}));
  const parsed = updateAttendanceSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await adminService.updateAttendance(id, parsed.data);
  return c.json(result);
});

api.delete("/admin/attendances/:id", async (c) => {
  const auth = c.req.header("Authorization");
  authService.getAdminFromBearer(auth);
  const id = c.req.param("id");
  const result = await adminService.deleteAttendance(id);
  return c.json(result);
});

const sectorsUpsertSchema = z.object({ id: z.string().min(1), name: z.string().min(1) });
const employeesUpsertSchema = z.object({
  id: z.string().min(1),
  name: z.string().min(1),
  sector_id: z.string().min(1),
  external_code: z.string().nullable().optional(),
});

api.post("/admin/sectors/upsert", async (c) => {
  const auth = c.req.header("Authorization");
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username !== "admin") return c.json({ error: "Forbidden" }, 403);
  } catch (e) {
    return c.json({ error: "Unauthorized" }, 401);
  }
  const body = await c.req.json().catch(() => ({}));
  const parsed = sectorsUpsertSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await adminService.upsertSector(parsed.data);
  return c.json(result, 200);
});

api.post("/admin/employees/upsert", async (c) => {
  const auth = c.req.header("Authorization");
  try {
    const admin = authService.getAdminFromBearer(auth);
    if (admin.username !== "admin") return c.json({ error: "Forbidden" }, 403);
  } catch (e) {
    return c.json({ error: "Unauthorized" }, 401);
  }
  const body = await c.req.json().catch(() => ({}));
  const parsed = employeesUpsertSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await adminService.upsertEmployee({
    ...parsed.data,
    external_code: parsed.data.external_code ?? null,
  });
  return c.json(result, 200);
});

// --- Dev (solo en development) ---
api.post("/dev/seed-min", async (c) => {
  const nodeEnv = (c.env?.NODE_ENV ?? process.env.NODE_ENV) as string | undefined;
  if (nodeEnv !== "development") {
    return c.json({ code: "not_found", message: "Not found" }, 404);
  }
  const result = await devService.seedMin();
  return c.json(result);
});

app.route("/api", api);

app.get("/", (c) => c.json({ name: "StaffAxis API", version: "1.0.0" }));

export const workerApp = app;
