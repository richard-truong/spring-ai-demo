"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import ProductCard from "@/components/ProductCard";
import type { Product } from "@/lib/types";

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api<Product[]>("/api/v1/products")
      .then(setProducts)
      .catch((err) =>
        setError(err instanceof Error ? err.message : "Failed to load products."),
      );
  }, []);

  if (error) return <p className="text-red-600">{error}</p>;
  if (!products) return <p className="text-gray-500">Loading products…</p>;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">Products</h1>
      <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {products.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
    </div>
  );
}
