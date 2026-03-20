import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import crypto from "node:crypto";

const SALT_ROUNDS = 10;
const JWT_SECRET = process.env.JWT_SECRET;
const DEVICE_TOKEN_BYTES = 32;

// --- Password (bcrypt) ---

export async function hashPassword(password: string): Promise<string> {
  return bcrypt.hash(password, SALT_ROUNDS);
}

export async function verifyPassword(
  password: string,
  hash: string
): Promise<boolean> {
  return bcrypt.compare(password, hash);
}

// --- Admin JWT (HS256) ---

export type AdminTokenPayload = {
  sub: string;  // admin user id
  username: string;
  iat?: number;
  exp?: number;
};

export function generateAdminToken(payload: Omit<AdminTokenPayload, "iat" | "exp">): string {
  if (!JWT_SECRET) throw new Error("JWT_SECRET es obligatorio en .env");
  return jwt.sign(
    payload,
    JWT_SECRET,
    { algorithm: "HS256", expiresIn: "36500d" }
  );
}

export function verifyAdminToken(token: string): AdminTokenPayload {
  if (!JWT_SECRET) throw new Error("JWT_SECRET es obligatorio en .env");
  const decoded = jwt.verify(token, JWT_SECRET, { algorithms: ["HS256"] });
  return decoded as AdminTokenPayload;
}

// --- Device API token ---
// HMAC-SHA256 para Workers (evita bcrypt CPU-heavy). Legacy bcrypt para tokens viejos.
const HMAC_PREFIX = "hmac:";
const DEVICE_TOKEN_SECRET =
  process.env.DEVICE_TOKEN_SECRET ?? process.env.JWT_SECRET ?? "staffaxis-device-secret";

export function generateDeviceToken(): string {
  return `sk_${crypto.randomBytes(DEVICE_TOKEN_BYTES).toString("hex")}`;
}

/** Hash rápido con HMAC. O(1) CPU vs bcrypt. */
function hashDeviceTokenHmac(token: string): string {
  const hmac = crypto.createHmac("sha256", DEVICE_TOKEN_SECRET);
  hmac.update(token);
  return HMAC_PREFIX + hmac.digest("hex");
}

/** Verifica token contra hash HMAC. */
function verifyDeviceTokenHmac(token: string, hash: string): boolean {
  const expected = hashDeviceTokenHmac(token);
  return expected === hash;
}

/** Hash HMAC síncrono para lookup O(1) en getDeviceFromToken. */
export function computeDeviceTokenHashForLookup(token: string): string {
  return hashDeviceTokenHmac(token);
}

export async function hashDeviceToken(token: string): Promise<string> {
  return Promise.resolve(hashDeviceTokenHmac(token));
}

export async function verifyDeviceTokenHash(
  token: string,
  hash: string
): Promise<boolean> {
  if (hash.startsWith(HMAC_PREFIX)) {
    return verifyDeviceTokenHmac(token, hash);
  }
  return bcrypt.compare(token, hash);
}
