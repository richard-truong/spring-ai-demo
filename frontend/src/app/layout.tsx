import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/components/AuthContext";
import { CartProvider } from "@/components/CartContext";
import Navbar from "@/components/Navbar";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "EvShop — Hexagonal Shop",
  description: "A simple storefront for the EvShop Spring Boot API",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full bg-gray-100 text-gray-900">
        <AuthProvider>
          <CartProvider>
            <div className="flex min-h-screen flex-col">
              <Navbar />
              <main className="mx-auto w-full max-w-6xl flex-1 px-4 py-8">{children}</main>
              <footer className="border-t border-gray-200 bg-white py-4 text-center text-xs text-gray-400">
                EvShop — lesson 9 demo UI
              </footer>
            </div>
          </CartProvider>
        </AuthProvider>
      </body>
    </html>
  );
}
