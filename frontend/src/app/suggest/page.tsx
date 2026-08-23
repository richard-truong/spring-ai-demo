"use client";

import { useState } from "react";
import { api } from "@/lib/api";
import type { Suggestion } from "@/lib/types";

const PLATFORMS = ["Instagram", "Facebook", "Shopee", "Lazada", "TikTok", "Other"];

export default function SuggestPage() {
  const [productName, setProductName] = useState("");
  const [platform, setPlatform] = useState("Shopee");
  const [result, setResult] = useState<Suggestion | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    setLoading(true);
    try {
      setResult(
        await api<Suggestion>("/api/v1/suggest", {
          method: "POST",
          body: { productName, platform },
          auth: false,
        }),
      );
    } catch (err) {
      setError(err instanceof Error ? err.message : "Suggestion failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">Product suggestion</h1>
      <p className="mt-1 text-sm text-gray-500">
        Get an AI-generated suggestion for selling a product on a platform.
      </p>

      <form
        onSubmit={submit}
        className="mt-6 max-w-md space-y-4 rounded-xl border border-gray-200 bg-white p-6"
      >
        <div>
          <label className="block text-sm font-medium text-gray-700">Product name</label>
          <input
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
            required
            placeholder="e.g. wireless headphones"
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Platform</label>
          <select
            value={platform}
            onChange={(e) => setPlatform(e.target.value)}
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none"
          >
            {PLATFORMS.map((p) => (
              <option key={p}>{p}</option>
            ))}
          </select>
        </div>
        <button
          type="submit"
          disabled={loading}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
        >
          {loading ? "Thinking…" : "Suggest"}
        </button>
      </form>

      {error && <p className="mt-4 text-sm text-red-600">{error}</p>}
      {result && (
        <div className="mt-6 max-w-md rounded-xl border border-blue-200 bg-blue-50 p-6">
          <h2 className="text-lg font-semibold text-gray-900">{result.name}</h2>
          <p className="mt-1 font-medium text-blue-700">{result.price}</p>
          <p className="mt-2 text-sm text-gray-600">{result.description}</p>
        </div>
      )}
    </div>
  );
}
