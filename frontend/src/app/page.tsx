import Link from "next/link";

const features = [
  {
    title: "Products",
    href: "/products",
    desc: "Browse the catalog, view details, and add items to your cart.",
  },
  {
    title: "Suggest",
    href: "/suggest",
    desc: "Get an AI-suggested product idea for a selling platform.",
  },
  {
    title: "Cart & Order",
    href: "/cart",
    desc: "Review your cart and place an order (requires login).",
  },
  {
    title: "AI Chat",
    href: "/chat",
    desc: "Chat with the AI assistant — needs the langchain4j profile enabled.",
  },
];

export default function Home() {
  return (
    <div>
      <div className="rounded-2xl bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white">
        <h1 className="text-3xl font-bold">EvShop</h1>
        <p className="mt-2 max-w-xl">
          A simple storefront that talks to the EvShop Spring Boot API — hexagonal
          architecture, JWT auth, rate limiting, caching, and AI features.
        </p>
      </div>
      <div className="mt-8 grid gap-4 sm:grid-cols-2">
        {features.map((f) => (
          <Link
            key={f.href}
            href={f.href}
            className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition-shadow hover:shadow"
          >
            <h2 className="text-lg font-semibold text-gray-900">{f.title}</h2>
            <p className="mt-1 text-sm text-gray-500">{f.desc}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
