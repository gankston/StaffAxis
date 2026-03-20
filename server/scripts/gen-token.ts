
import dotenv from "dotenv";
import path from "path";
import { generateAdminToken } from "../src/utils/security.js";

// Cargar variables de entorno
dotenv.config({ path: path.join(process.cwd(), ".env") });

const payload = {
  sub: "test-admin-id",
  username: "admin-test"
};

try {
  const token = generateAdminToken(payload);
  console.log("\n--- TOKEN DE PRUEBA GENERADO ---");
  console.log(token);
  console.log("--------------------------------\n");
} catch (error) {
  console.error("Error al generar el token:", error);
}
