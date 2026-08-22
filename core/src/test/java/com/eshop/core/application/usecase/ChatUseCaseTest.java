package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;
import com.eshop.core.application.port.out.ChatMemoryPort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatUseCaseTest {

    @Test
    void replyDelegatesWithMemoryIdBuiltFromUserAndSession() {
        RecordingChatMemoryPort port = new RecordingChatMemoryPort();
        port.replyResult = new ChatReply("Hi there");
        ChatUseCaseImpl useCase = new ChatUseCaseImpl(port);

        ChatReply reply = useCase.reply(new ChatCommand("user-1", "session-42", "hello"));

        assertThat(reply.content()).isEqualTo("Hi there");
        assertThat(port.replyMemoryId).isEqualTo(new ChatMemoryId("user-1", "session-42"));
        assertThat(port.replyMessage).isEqualTo("hello");
    }

    @Test
    void historyIsIsolatedPerUserEvenWithSameSession() {
        RecordingChatMemoryPort port = new RecordingChatMemoryPort();
        ChatUseCaseImpl useCase = new ChatUseCaseImpl(port);

        useCase.history("user-1", "session-1");
        useCase.history("user-2", "session-1");

        assertThat(port.historyMemoryIds).containsExactly(
            new ChatMemoryId("user-1", "session-1"),
            new ChatMemoryId("user-2", "session-1")
        );
    }

    @Test
    void clearDelegatesWithMemoryIdBuiltFromUserAndSession() {
        RecordingChatMemoryPort port = new RecordingChatMemoryPort();
        ChatUseCaseImpl useCase = new ChatUseCaseImpl(port);

        useCase.clear("user-1", "session-1");

        assertThat(port.clearMemoryId).isEqualTo(new ChatMemoryId("user-1", "session-1"));
    }

    private static final class RecordingChatMemoryPort implements ChatMemoryPort {

        ChatReply replyResult;
        ChatMemoryId replyMemoryId;
        String replyMessage;
        final List<ChatMemoryId> historyMemoryIds = new ArrayList<>();
        ChatMemoryId clearMemoryId;

        @Override
        public ChatReply reply(ChatMemoryId memoryId, String userMessage) {
            this.replyMemoryId = memoryId;
            this.replyMessage = userMessage;
            return replyResult;
        }

        @Override
        public List<ChatMessage> history(ChatMemoryId memoryId) {
            this.historyMemoryIds.add(memoryId);
            return List.of();
        }

        @Override
        public void clear(ChatMemoryId memoryId) {
            this.clearMemoryId = memoryId;
        }

    }

}
