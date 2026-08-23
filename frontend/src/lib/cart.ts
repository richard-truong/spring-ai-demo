export interface CartLine {
  productId: string;
  name: string;
  price: number;
  currency: string;
  quantity: number;
}

const CART_KEY = "eshop_cart";

export function getCart(): CartLine[] {
  if (typeof window === "undefined") return [];
  const raw = window.localStorage.getItem(CART_KEY);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as CartLine[];
  } catch {
    return [];
  }
}

export function setCart(lines: CartLine[]): void {
  window.localStorage.setItem(CART_KEY, JSON.stringify(lines));
}

export function clearCart(): void {
  window.localStorage.removeItem(CART_KEY);
}
