"use client";

import { useState } from "react";
import Link from "next/link";
import { api, ApiError } from "@/lib/api";
import { clearAuth } from "@/lib/auth";
import { useAuth } from "@/components/AuthContext";
import { useCart } from "@/components/CartContext";
import OrderSummary from "@/components/OrderSummary";
import type { OrderResponse } from "@/lib/types";

export default function CartPage() {
  const { lines, setQty, remove, total, clear } = useCart();
  const { user } = useAuth();
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [placing, setPlacing] = useState(false);

  const placeOrder = async () => {
    setError(null);
    setPlacing(true);
    try {
      const created = await api<OrderResponse>("/api/v1/orders", {
        method: "POST",
        body: {
          items: lines.map((l) => ({ productId: l.productId, quantity: l.quantity })),
        },
      });
      setOrder(created);
      clear();
    } catch (err) {
      const msg = err instanceof Error ? err.message : "Failed to place order.";
      if (err instanceof ApiError && err.status === 401) {
        clearAuth();
      }
      setError(msg);
    } finally {
      setPlacing(false);
    }
  };

  if (order) return <OrderSummary order={order} />;

  if (lines.length === 0) {
    return (
      <div className="rounded-xl border border-gray-200 bg-white p-8 text-center">
        <h1 className="text-xl font-bold text-gray-900">Your cart is empty</h1>
        <p className="mt-1 text-sm text-gray-500">Add some products to get started.</p>
        <Link
          href="/products"
          className="mt-4 inline-block rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
        >
          Browse products
        </Link>
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">Your cart</h1>
      <div className="mt-6 space-y-3">
        {lines.map((l) => (
          <div
            key={l.productId}
            className="flex items-center justify-between rounded-xl border border-gray-200 bg-white p-4"
          >
            <div>
              <p className="font-medium text-gray-900">{l.name}</p>
              <p className="text-sm text-gray-500">{l.price.toLocaleString()} đ each</p>
            </div>
            <div className="flex items-center gap-3">
              <input
                type="number"
                min={1}
                max={99}
                value={l.quantity}
                onChange={(e) => setQty(l.productId, Math.max(1, Number(e.target.value)))}
                className="w-16 rounded-lg border border-gray-300 px-2 py-1 text-center text-sm"
              />
              <button
                onClick={() => remove(l.productId)}
                className="text-sm text-red-600 hover:underline"
              >
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>
      <div className="mt-6 flex flex-wrap items-center justify-between gap-2 rounded-xl border border-gray-200 bg-white p-4">
        <span className="font-semibold text-gray-900">Total: {total.toLocaleString()} đ</span>
        {!user && (
          <p className="text-sm text-amber-600">You&apos;ll need to log in to place this order.</p>
        )}
      </div>
      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
      <button
        onClick={placeOrder}
        disabled={placing}
        className="mt-4 rounded-lg bg-green-600 px-6 py-2.5 text-sm font-semibold text-white hover:bg-green-700 disabled:opacity-60"
      >
        {placing ? "Placing order…" : "Place order"}
      </button>
    </div>
  );
}
