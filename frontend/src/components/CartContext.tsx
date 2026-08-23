"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { clearCart, getCart, setCart } from "@/lib/cart";
import type { CartLine } from "@/lib/cart";

interface CartContextValue {
  lines: CartLine[];
  count: number;
  total: number;
  addToCart: (line: CartLine) => void;
  setQty: (productId: string, quantity: number) => void;
  remove: (productId: string) => void;
  clear: () => void;
}

const CartContext = createContext<CartContextValue | undefined>(undefined);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const [lines, setLines] = useState<CartLine[]>([]);

  useEffect(() => {
    setLines(getCart());
  }, []);

  const persist = useCallback((next: CartLine[]) => {
    setCart(next);
    setLines(next);
  }, []);

  const addToCart = useCallback(
    (line: CartLine) => {
      const next = [...lines];
      const existing = next.find((l) => l.productId === line.productId);
      if (existing) {
        existing.quantity += line.quantity;
      } else {
        next.push(line);
      }
      persist(next);
    },
    [lines, persist],
  );

  const setQty = useCallback(
    (productId: string, quantity: number) => {
      const next = lines
        .map((l) => (l.productId === productId ? { ...l, quantity } : l))
        .filter((l) => l.quantity > 0);
      persist(next);
    },
    [lines, persist],
  );

  const remove = useCallback(
    (productId: string) => {
      persist(lines.filter((l) => l.productId !== productId));
    },
    [lines, persist],
  );

  const clear = useCallback(() => {
    clearCart();
    setLines([]);
  }, []);

  const count = useMemo(() => lines.reduce((sum, l) => sum + l.quantity, 0), [lines]);
  const total = useMemo(() => lines.reduce((sum, l) => sum + l.price * l.quantity, 0), [lines]);

  const value = useMemo(
    () => ({ lines, count, total, addToCart, setQty, remove, clear }),
    [lines, count, total, addToCart, setQty, remove, clear],
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart(): CartContextValue {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart must be used within CartProvider");
  return ctx;
}
