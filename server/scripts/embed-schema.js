#!/usr/bin/env node
/**
 * Embeds db/schema.sql into src/bootstrap/schemaContent.ts for Cloudflare Workers.
 * Workers no tiene fs; el schema debe estar embebido en el bundle.
 */
const fs = require("fs");
const path = require("path");

const schemaPath = path.join(__dirname, "..", "..", "db", "schema.sql");
const outPath = path.join(__dirname, "..", "src", "bootstrap", "schemaContent.ts");

if (!fs.existsSync(schemaPath)) {
  console.error("No se encontró db/schema.sql");
  process.exit(1);
}

const schema = fs.readFileSync(schemaPath, "utf-8");
const content = `/**
 * Schema embebido para Cloudflare Workers (sin fs).
 * Generado por scripts/embed-schema.js - no editar manualmente.
 */
export const schemaSql = ${JSON.stringify(schema)};
`;

fs.writeFileSync(outPath, content);
console.log("Schema embebido en src/bootstrap/schemaContent.ts");
