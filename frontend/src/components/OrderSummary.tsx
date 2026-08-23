import type { OrderResponse } from "@/lib/types";

export default function OrderSummary({ order }: { order: OrderResponse }) {
  return (
    <div className="rounded-xl border border-green-200 bg-green-50 p-6">
      <h2 className="text-lg font-bold text-green-800">Order placed ✓</h2>
      <p className="mt-1 text-sm text-green-700">
        Order <span className="font-mono">{order.id}</span> · {order.status}
      </p>
      <ul className="mt-4 space-y-2">
        {order.items.map((item) => (
          <li key={item.productId} className="flex justify-between text-sm text-gray-700">
            <span>
              {item.name} × {item.quantity}
            </span>
            <span>{item.subtotal.toLocaleString()} đ</span>
          </li>
        ))}
      </ul>
      <p className="mt-4 border-t border-green-200 pt-3 font-semibold text-gray-900">
        Total: {order.total.toLocaleString()} đ
      </p>
      <p className="mt-1 text-xs text-green-600">
        Created at {new Date(order.createdAt).toLocaleString()}
      </p>
    </div>
  );
}
