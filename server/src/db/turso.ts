import {
  createClient,
  type Client,
  type Transaction,
  type InValue,
} from "@libsql/client";

const url = process.env.TURSO_DATABASE_URL;
const authToken = process.env.TURSO_AUTH_TOKEN;

if (!url || !authToken) {
  throw new Error(
    "TURSO_DATABASE_URL y TURSO_AUTH_TOKEN son obligatorios en .env"
  );
}

const client = createClient({
  url,
  authToken,
});

export { client as db };

export type { Client, Transaction, InValue };

/**
 * Ejecuta una consulta parametrizada.
 * @param sql - Query SQL con placeholders (?)
 * @param params - Parámetros en orden
 */
export async function query(
  sql: string,
  params: InValue[] = []
): Promise<{ rows: unknown[] }> {
  const result = await client.execute({
    sql,
    args: params,
  });
  return { rows: result.rows };
}

/**
 * Ejecuta una función dentro de una transacción.
 * Hace commit automático si fn termina bien, rollback si lanza.
 */
export async function transaction<T>(
  fn: (tx: Transaction) => Promise<T>
): Promise<T> {
  const tx = await client.transaction("write");
  try {
    const result = await fn(tx);
    await tx.commit();
    return result;
  } catch (err) {
    await tx.rollback();
    throw err;
  } finally {
    tx.close();
  }
}
