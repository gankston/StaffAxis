import { Router } from "express";
import { healthRouter } from "./health.js";
import { sectorsRouter } from "./sectors.js";
import { authRouter } from "./auth.js";
import { submissionsRouter } from "./submissions.js";
import { approvedRouter } from "./approved.js";

export const router = Router();

router.use("/health", healthRouter);
router.use("/sectors", sectorsRouter);
router.use("/auth", authRouter);
router.use("/submissions", submissionsRouter);
router.use("/approved", approvedRouter);
