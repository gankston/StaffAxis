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
import { logger } from "./utils/logger.js";

const dateString = z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "date formato YYYY-MM-DD");

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
  return c.json(result);
});
api.post("/auth/admin/login", zValidator("json", loginSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await authService.adminLogin(body);
  return c.json(result);
});

api.post("/auth/device/register", zValidator("json", registerDeviceSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await authService.registerDevice(body);
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

// --- Sectors ---
const createSectorSchema = z.object({ name: z.string().min(1).max(100) });
api.get("/sectors", async (c) => {
  const result = await sectorsService.listSectors();
  return c.json(result);
});
api.post("/sectors", zValidator("json", createSectorSchema), async (c) => {
  const body = c.req.valid("json");
  const result = await sectorsService.createSector(body);
  return c.json({ id: result.id, name: result.name }, 201);
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

api.post("/submissions", async (c) => {
  const token = c.req.header("X-Device-Token");
  const device = await authService.getDeviceFromToken(token);
  const body = await c.req.json().catch(() => ({}));
  const parsed = createSubmissionSchema.safeParse(body);
  if (!parsed.success) {
    const first = Object.values(parsed.error.flatten().fieldErrors).flat()[0];
    return c.json({ code: "validation_error", message: (first as string) ?? "Datos inválidos" }, 400);
  }
  const result = await submissionsService.createSubmission(device, parsed.data);
  if (result.status === 400) {
    return c.json(result.data, 400);
  }
  return c.json(result.data, result.status);
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
api.get("/approved", async (c) => {
  const token = c.req.header("X-Device-Token");
  const device = await authService.getDeviceFromToken(token);
  const query = parseQuery(approvedQuerySchema, c.req.query());
  const result = await approvedService.getApproved(device, query.since ?? 0);
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
  authService.getAdminFromBearer(auth);
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
  authService.getAdminFromBearer(auth);
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
