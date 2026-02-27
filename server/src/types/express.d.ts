export type DeviceAuth = {
  device_id: string;
  sector_id: string;
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
    }
  }
}
