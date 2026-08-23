"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import { setToken } from "@/lib/auth";
import { useAuth } from "./AuthContext";
import type { TokenResponse, UserResponse } from "@/lib/types";

export default function AuthForm() {
  const { login } = useAuth();
  const [mode, setMode] = useState<"login" | "register">("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      if (mode === "register") {
        const user = await api<UserResponse>("/api/v1/auth/register", {
          method: "POST",
          body: { email, password, name },
          auth: false,
        });
        login(user);
      } else {
        const res = await api<TokenResponse>("/api/v1/auth/login", {
          method: "POST",
          body: { email, password },
          auth: false,
        });
        setToken(res.accessToken);
        login({ email });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  const inputCls =
    "w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none";

  return (
    <div className="mx-auto mt-10 w-full max-w-sm rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
      <div className="mb-4 flex rounded-lg bg-gray-100 p-1">
        <button
          type="button"
          onClick={() => setMode("login")}
          className={`flex-1 rounded-md px-3 py-1.5 text-sm font-medium ${
            mode === "login" ? "bg-white shadow text-gray-900" : "text-gray-500"
          }`}
        >
          Log in
        </button>
        <button
          type="button"
          onClick={() => setMode("register")}
          className={`flex-1 rounded-md px-3 py-1.5 text-sm font-medium ${
            mode === "register" ? "bg-white shadow text-gray-900" : "text-gray-500"
          }`}
        >
          Register
        </button>
      </div>

      <form onSubmit={submit} className="space-y-3">
        {mode === "register" && (
          <input
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={inputCls}
            required
          />
        )}
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className={inputCls}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className={inputCls}
          required
        />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
        >
          {loading ? "Please wait…" : mode === "login" ? "Log in" : "Create account"}
        </button>
      </form>
      <p className="mt-3 text-xs text-gray-400">
        Tip: auth endpoints are rate-limited to ~5 requests per minute per IP.
      </p>
    </div>
  );
}
