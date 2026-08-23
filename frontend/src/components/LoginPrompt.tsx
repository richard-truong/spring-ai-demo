import Link from "next/link";

export default function LoginPrompt() {
  return (
    <div className="rounded-xl border border-amber-300 bg-amber-50 p-6 text-amber-800">
      <h2 className="font-semibold">Log in to continue</h2>
      <p className="mt-1 text-sm">
        This section requires an authenticated session.
      </p>
      <Link
        href="/auth"
        className="mt-3 inline-block rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
      >
        Go to login
      </Link>
    </div>
  );
}
