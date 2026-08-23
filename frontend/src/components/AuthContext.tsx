"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { clearAuth, getUser, setUser } from "@/lib/auth";
import type { StoredUser } from "@/lib/types";

interface AuthContextValue {
  user: StoredUser | null;
  login: (user: StoredUser) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<StoredUser | null>(null);

  useEffect(() => {
    setUserState(getUser());
  }, []);

  const login = useCallback((next: StoredUser) => {
    setUser(next);
    setUserState(next);
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    setUserState(null);
  }, []);

  const value = useMemo(() => ({ user, login, logout }), [user, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
