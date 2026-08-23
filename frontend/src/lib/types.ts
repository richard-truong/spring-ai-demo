export interface Price {
  amount: number;
  currency: string;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: Price;
  stock: number;
}

export interface UserResponse {
  id: string;
  email: string;
  name: string;
}

export type StoredUser = Partial<UserResponse>;

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface OrderItemResponse {
  productId: string;
  name: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface OrderResponse {
  id: string;
  userId: string;
  items: OrderItemResponse[];
  total: number;
  status: string;
  createdAt: string;
}

export interface Suggestion {
  name: string;
  price: string;
  description: string;
}

export interface ChatMessage {
  role: string;
  content: string;
}

export interface ChatHistory {
  sessionId: string;
  messages: ChatMessage[];
}
