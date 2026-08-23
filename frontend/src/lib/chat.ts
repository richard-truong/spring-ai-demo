import { API_URL, toApiError } from "./api";

export interface StreamChatOptions {
  sessionId: string;
  message: string;
  token: string;
  onToken: (token: string) => void;
  onDone: () => void;
}

export async function streamChat(opts: StreamChatOptions): Promise<void> {
  const res = await fetch(`${API_URL}/api/v1/chat/stream`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${opts.token}`,
    },
    body: JSON.stringify({ sessionId: opts.sessionId, message: opts.message }),
  });

  if (!res.ok) throw await toApiError(res);
  if (!res.body) {
    opts.onDone();
    return;
  }

  const reader = res.body.getReader();
  const decoder = new TextDecoder();
  let buffer = "";

  for (;;) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });

    let sep = buffer.indexOf("\n\n");
    while (sep !== -1) {
      const event = buffer.slice(0, sep);
      buffer = buffer.slice(sep + 2);
      for (const line of event.split("\n")) {
        if (line.startsWith("data:")) {
          const data = line.slice(5);
          if (data.trim()) opts.onToken(data);
        }
      }
      sep = buffer.indexOf("\n\n");
    }
  }

  // flush any remaining data without a trailing blank line
  for (const line of buffer.split("\n")) {
    if (line.startsWith("data:")) {
      const data = line.slice(5);
      if (data.trim()) opts.onToken(data);
    }
  }

  opts.onDone();
}

export function newSessionId(): string {
  if (typeof crypto !== "undefined" && "randomUUID" in crypto) {
    return crypto.randomUUID();
  }
  return `session-${Date.now()}-${Math.random().toString(36).slice(2)}`;
}
