"use client";

import Link from "next/link";
import { useCart } from "./CartContext";
import type { Product } from "@/lib/types";

export function formatPrice(amount: number, currency: string): string {
  try {
    return new Intl.NumberFormat("en-US", { style: "currency", currency }).format(amount);
  } catch {
    return `${currency} ${amount}`;
  }
}

export default function ProductCard({ product }: { product: Product }) {
  const { addToCart } = useCart();

  const outOfStock = product.stock <= 0;

  return (
    <div className="flex flex-col rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
      <Link href={`/products/${product.id}`}>
        <h3 className="font-semibold text-gray-900 hover:text-blue-600">{product.name}</h3>
      </Link>
      <p className="mt-1 line-clamp-2 flex-1 text-sm text-gray-500">{product.description}</p>
      <div className="mt-3 flex items-center justify-between">
        <span className="font-bold text-gray-900">
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
        className="mt-3 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-300 disabled:text-gray-500"
      >
        Add to cart
      </button>
    </div>
  );
}
