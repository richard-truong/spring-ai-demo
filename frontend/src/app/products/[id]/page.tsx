"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { api } from "@/lib/api";
import { formatPrice } from "@/components/ProductCard";
import { useCart } from "@/components/CartContext";
import type { Product } from "@/lib/types";

export default function ProductDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params?.id ?? "";
  const { addToCart } = useCart();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    api<Product>(`/api/v1/products/${id}`)
      .then(setProduct)
      .catch((err) =>
        setError(err instanceof Error ? err.message : "Failed to load product."),
      );
  }, [id]);

  if (error) return <p className="text-red-600">{error}</p>;
  if (!product) return <p className="text-gray-500">Loading…</p>;

  const outOfStock = product.stock <= 0;

  return (
    <div className="rounded-xl border border-gray-200 bg-white p-6">
      <Link href="/products" className="text-sm text-blue-600 hover:underline">
        ← Back to products
      </Link>
      <h1 className="mt-2 text-2xl font-bold text-gray-900">{product.name}</h1>
      <p className="mt-2 text-gray-600">{product.description}</p>
      <div className="mt-4 flex items-center gap-4">
        <span className="text-2xl font-bold text-gray-900">
          {formatPrice(product.price.amount, product.price.currency)}
        </span>
        <span
          className={`rounded-full px-2 py-0.5 text-xs font-medium ${
            outOfStock ? "bg-red-100 text-red-700" : "bg-green-100 text-green-700"
          }`}
        >
          {outOfStock ? "Out of stock" : `${product.stock} in stock`}
        </span>
      </div>
      <button
        onClick={() =>
          addToCart({
            productId: product.id,
            name: product.name,
            price: product.price.amount,
            currency: product.price.currency,
            quantity: 1,
          })
        }
        disabled={outOfStock}
        className="mt-6 rounded-lg bg-blue-600 px-6 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-500"
      >
        Add to cart
      </button>
    </div>
  );
}
