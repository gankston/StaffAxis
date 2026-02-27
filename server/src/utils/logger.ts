const prefix = "[StaffAxis]";

export const logger = {
  info: (...args: unknown[]) => console.log(prefix, ...args),
  warn: (...args: unknown[]) => console.warn(prefix, ...args),
  error: (...args: unknown[]) => console.error(prefix, ...args),
  debug: (...args: unknown[]) => {
    if (process.env.NODE_ENV === "development") {
      console.debug(prefix, ...args);
    }
  },
};
