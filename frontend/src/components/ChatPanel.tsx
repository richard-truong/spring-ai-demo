"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { api } from "@/lib/api";
import { streamChat, newSessionId } from "@/lib/chat";
import { getToken } from "@/lib/auth";
import { useAuth } from "./AuthContext";
import type { ChatHistory, ChatMessage } from "@/lib/types";

const SESSION_KEY = "eshop_chat_session";

type ChatStatus = "loading" | "ready" | "unavailable" | "login";

export default function ChatPanel() {
  const { user } = useAuth();
  const [sessionId, setSessionId] = useState<string>(() => {
    if (typeof window === "undefined") return "";
    return window.localStorage.getItem(SESSION_KEY) ?? "";
  });
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [status, setStatus] = useState<ChatStatus>("loading");
  const [sending, setSending] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);

  const ensureSession = useCallback(async () => {
    if (!sessionId) {
      setSessionId(newSessionId());
      setStatus("ready");
      return;
    }
    try {
      const history = await api<ChatHistory>(`/api/v1/chat/${sessionId}/history`);
      setMessages(history.messages ?? []);
      setStatus("ready");
    } catch (err) {
      if (err instanceof Error && "status" in err && (err as { status: number }).status === 404) {
        setStatus("unavailable");
      } else if (
        err instanceof Error &&
        "status" in err &&
        (err as { status: number }).status === 401
      ) {
        setStatus("login");
      } else {
        setStatus("ready");
      }
    }
  }, [sessionId]);

  useEffect(() => {
    ensureSession();
  }, [ensureSession]);

  useEffect(() => {
    if (sessionId) window.localStorage.setItem(SESSION_KEY, sessionId);
  }, [sessionId]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const send = async (e: React.FormEvent) => {
    e.preventDefault();
    const text = input.trim();
    if (!text || sending) return;
    const token = getToken();
    if (!token) {
      setStatus("login");
      return;
    }
    const myId = sessionId || newSessionId();
    if (!sessionId) setSessionId(myId);

    setMessages((prev) => [...prev, { role: "user", content: text }, { role: "assistant", content: "" }]);
    setInput("");
    setSending(true);

    let index = -1;
    const setAssistantToken = (t: string) => {
      setMessages((prev) => {
        if (index === -1) index = prev.length - 1;
        const next = [...prev];
        next[index] = { role: "assistant", content: next[index].content + t };
        return next;
      });
    };

    try {
      await streamChat({
        sessionId: myId,
        message: text,
        token,
        onToken: setAssistantToken,
        onDone: () => {},
      });
    } catch (err) {
      setMessages((prev) => {
        const next = [...prev];
        const last = next[next.length - 1];
        if (last?.role === "assistant" && !last.content) {
          next[next.length - 1] = {
            role: "assistant",
            content: err instanceof Error ? err.message : "Request failed.",
          };
        }
        return next;
      });
    } finally {
      setSending(false);
    }
  };

  const newConversation = async () => {
    if (sessionId && getToken()) {
      try {
        await api(`/api/v1/chat/${sessionId}`, { method: "DELETE" });
      } catch {
        // backend may be unavailable; ignore
      }
    }
    const next = newSessionId();
    setSessionId(next);
    setMessages([]);
    setStatus("ready");
  };

  if (status === "loading") {
    return <p className="text-sm text-gray-400">Loading conversation…</p>;
  }

  if (status === "unavailable") {
    return (
      <div className="rounded-xl border border-amber-300 bg-amber-50 p-6 text-amber-800">
        <h2 className="font-semibold">Chat is not available</h2>
        <p className="mt-1 text-sm">
          The chat endpoints only exist when the backend runs with the{" "}
          <code className="rounded bg-amber-100 px-1">langchain4j</code> Spring profile enabled.
          Restart the backend with the profile to enable AI chat.
        </p>
      </div>
    );
  }

  if (status === "login") {
    return (
      <div className="rounded-xl border border-gray-200 bg-white p-6">
        <h2 className="font-semibold text-gray-900">Log in to chat</h2>
        <p className="mt-1 text-sm text-gray-500">Chat requires a logged-in session.</p>
        <a
          href="/auth"
          className="mt-3 inline-block rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
        >
          Go to login
        </a>
      </div>
    );
  }

  return (
    <div className="flex h-[480px] flex-col rounded-xl border border-gray-200 bg-white shadow-sm">
      <div className="flex items-center justify-between border-b border-gray-100 px-4 py-3">
        <h2 className="font-semibold text-gray-900">AI Chat</h2>
        <button
          onClick={newConversation}
          className="rounded-lg border border-gray-300 px-3 py-1 text-xs font-medium text-gray-600 hover:bg-gray-50"
        >
          New conversation
        </button>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto p-4">
        {messages.length === 0 && (
          <p className="text-center text-sm text-gray-400">
            Ask about products, recommendations, or anything about the shop.
          </p>
        )}
        {messages.map((m, i) => (
          <div key={i} className={m.role === "user" ? "flex justify-end" : "flex justify-start"}>
            <div
              className={`max-w-[80%] whitespace-pre-wrap rounded-lg px-3 py-2 text-sm ${
                m.role === "user"
                  ? "bg-blue-600 text-white"
                  : "border border-gray-200 bg-gray-50 text-gray-800"
              }`}
            >
              {m.content || (i === messages.length - 1 && sending ? "…" : "")}
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={send} className="flex gap-2 border-t border-gray-100 p-3">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type a message…"
          disabled={sending}
          className="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none disabled:opacity-60"
        />
        <button
          type="submit"
          disabled={sending || !input.trim()}
          className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
        >
          Send
        </button>
      </form>
    </div>
  );
}
