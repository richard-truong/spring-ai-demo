import ChatPanel from "@/components/ChatPanel";

export default function ChatPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900">AI Chat</h1>
      <p className="mt-1 text-sm text-gray-500">
        Streams responses from the backend. Requires the backend to run with the{" "}
        <code className="rounded bg-gray-200 px-1">langchain4j</code> profile.
      </p>
      <div className="mt-6">
        <ChatPanel />
      </div>
    </div>
  );
}
