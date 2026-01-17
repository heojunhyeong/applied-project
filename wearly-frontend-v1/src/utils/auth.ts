export type UserRole = "USER" | "SELLER" | "ADMIN";

type JwtPayload = {
  role?: unknown;
  userRole?: unknown;
};

const normalizeRole = (value: unknown): UserRole | null => {
  if (!value) return null;
  const roleValue = Array.isArray(value) ? value[0] : value;
  if (typeof roleValue !== "string") return null;
  const upper = roleValue.toUpperCase();
  if (upper.includes("ADMIN")) return "ADMIN";
  if (upper.includes("SELLER")) return "SELLER";
  if (upper.includes("USER")) return "USER";
  return null;
};

export const getAccessToken = () => localStorage.getItem("accessToken");

export const decodeJwtPayload = (token: string): JwtPayload | null => {
  const parts = token.split(".");
  if (parts.length !== 3) return null;

  try {
    const payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const padded = payload.padEnd(payload.length + ((4 - (payload.length % 4)) % 4), "=");
    const decoded = atob(padded);
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
};

export const getRoleFromToken = (token: string | null): UserRole | null => {
  if (!token) return null;
  const payload = decodeJwtPayload(token);
  if (!payload) return null;
  return normalizeRole(payload.role ?? payload.userRole);
};
