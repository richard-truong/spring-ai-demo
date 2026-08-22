package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;
import com.eshop.core.domain.vo.ChatRole;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LangChain4jChatMemoryAdapterTest {

    private static final ChatMemoryId MEMORY_ID = new ChatMemoryId("user-1", "session-1");

    private final ChatAssistant assistant = mock(ChatAssistant.class);
    private final ChatMemoryStore store = mock(ChatMemoryStore.class);
    private final LangChain4jChatMemoryAdapter adapter = new LangChain4jChatMemoryAdapter(assistant, store);

    @Test
    void replyDelegatesToAssistant() {
        when(assistant.chat(MEMORY_ID, "hello")).thenReturn("Hello!");

        ChatReply reply = adapter.reply(MEMORY_ID, "hello");

        assertThat(reply.content()).isEqualTo("Hello!");
    }

    @Test
    void historyFiltersSystemMessageAndMapsToDomain() {
        when(store.getMessages(MEMORY_ID)).thenReturn(List.of(
            new SystemMessage("You are a helpful assistant"),
            new UserMessage("hi"),
            new AiMessage("hello there")
        ));

        List<ChatMessage> history = adapter.history(MEMORY_ID);

        assertThat(history).containsExactly(
            new ChatMessage(ChatRole.USER, "hi"),
            new ChatMessage(ChatRole.AI, "hello there")
        );
    }

    @Test
    void clearDeletesMemory() {
        adapter.clear(MEMORY_ID);

        verify(store).deleteMessages(MEMORY_ID);
    }

}
