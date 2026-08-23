"use client";

import Link from "next/link";
import { useAuth } from "./AuthContext";
import { useCart } from "./CartContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const { count } = useCart();

  const linkCls =
    "px-3 py-2 rounded-lg text-sm font-medium text-gray-200 hover:bg-gray-700 hover:text-white transition-colors";

  return (
    <header className="bg-gray-900 text-white shadow">
      <nav className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <div className="flex items-center gap-6">
          <Link href="/" className="text-lg font-bold tracking-tight">
            EvShop
          </Link>
          <div className="flex items-center gap-1">
            <Link href="/products" className={linkCls}>
              Products
            </Link>
            <Link href="/suggest" className={linkCls}>
              Suggest
            </Link>
            <Link href="/chat" className={linkCls}>
              Chat
            </Link>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <Link href="/cart" className={linkCls}>
            Cart {count > 0 && <span className="ml-1 rounded-full bg-blue-500 px-2 py-0.5 text-xs">{count}</span>}
          </Link>
          {user ? (
            <>
              <span className="text-sm text-gray-300">{user.email}</span>
              <button
                onClick={logout}
                className="rounded-lg px-3 py-2 text-sm font-medium text-gray-200 hover:bg-gray-700"
              >
                Log out
              </button>
            </>
          ) : (
            <Link href="/auth" className={linkCls}>
              Log in
            </Link>
          )}
        </div>
      </nav>
    </header>
  );
}
