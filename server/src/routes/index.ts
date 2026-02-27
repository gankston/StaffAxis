import { Router } from "express";
import { healthRouter } from "./health.js";
import { sectorsRouter } from "./sectors.js";

export const router = Router();

router.use("/health", healthRouter);
router.use("/sectors", sectorsRouter);
