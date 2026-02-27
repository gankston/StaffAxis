export type DeviceAuth = {
  device_id: string;
  sector_id: string;
  encargado_name: string;
  device_db_id: string;
};

declare global {
  namespace Express {
    interface Request {
      adminUser?: {
        sub: string;
        username: string;
        iat?: number;
        exp?: number;
      };
      deviceAuth?: DeviceAuth;
      /** Query validada por validateQuery (zod). Tipado por cada ruta. */
      validatedQuery?: Record<string, unknown>;
    }
  }
}
