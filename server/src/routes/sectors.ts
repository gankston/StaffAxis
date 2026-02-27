import { Router } from "express";
import { z } from "zod";
import { db } from "../db/turso.js";
import { validate } from "../middleware/validate.js";
import { AppError } from "../middleware/errorHandler.js";

export const sectorsRouter = Router();

const createSectorSchema = z.object({
  name: z.string().min(1, "Nombre requerido").max(100),
});

sectorsRouter.get("/", async (_req, res) => {
  const result = await db.execute("SELECT id, name FROM sectors ORDER BY name");
  res.json({ sectors: result.rows });
});

sectorsRouter.post(
  "/",
  validate(createSectorSchema),
  async (req, res) => {
    const { name } = req.body as z.infer<typeof createSectorSchema>;
    const id = crypto.randomUUID();
    try {
      await db.execute({
        sql: "INSERT INTO sectors (id, name) VALUES (?, ?)",
        args: [id, name],
      });
    } catch (err) {
      const msg = err instanceof Error ? err.message : String(err);
      if (msg.includes("UNIQUE") || msg.includes("unique")) {
        throw new AppError(409, "Sector con ese nombre ya existe", "conflict");
      }
      throw err;
    }
    res.status(201).json({ id, name });
  }
);
