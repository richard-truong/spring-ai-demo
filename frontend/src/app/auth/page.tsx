import AuthForm from "@/components/AuthForm";

export default function AuthPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">Account</h1>
      <p className="mt-1 text-sm text-gray-500">
        Log in or create an account to place orders.
      </p>
      <AuthForm />
    </div>
  );
}
