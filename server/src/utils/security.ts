import bcrypt from "bcrypt";
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
    { algorithm: "HS256", expiresIn: "7d" }
  );
}

export function verifyAdminToken(token: string): AdminTokenPayload {
  if (!JWT_SECRET) throw new Error("JWT_SECRET es obligatorio en .env");
  const decoded = jwt.verify(token, JWT_SECRET, { algorithms: ["HS256"] });
  return decoded as AdminTokenPayload;
}

// --- Device API token ---

export function generateDeviceToken(): string {
  return `sk_${crypto.randomBytes(DEVICE_TOKEN_BYTES).toString("hex")}`;
}

export async function hashDeviceToken(token: string): Promise<string> {
  return bcrypt.hash(token, SALT_ROUNDS);
}

export async function verifyDeviceTokenHash(
  token: string,
  hash: string
): Promise<boolean> {
  return bcrypt.compare(token, hash);
}
