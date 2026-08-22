package com.eshop.app.memory;

import com.eshop.app.support.PostgresIntegrationTest;
import com.eshop.core.application.dto.ChatMemoryId;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JpaChatMemoryStoreTest extends PostgresIntegrationTest {

    private static final ChatMemoryId MEMORY_ID = new ChatMemoryId("user-1", "session-roundtrip");

    @Autowired
    ChatMemoryJpaRepository repository;

    @Test
    void messagesRoundTrip() {
        JpaChatMemoryStore store = new JpaChatMemoryStore(repository);

        store.updateMessages(MEMORY_ID, List.of(
            new SystemMessage("system"),
            new UserMessage("hi"),
            new AiMessage("hello")
        ));

        assertThat(store.getMessages(MEMORY_ID)).containsExactly(
            new SystemMessage("system"),
            new UserMessage("hi"),
            new AiMessage("hello")
        );
    }

    @Test
    void storesUserIdAndSessionIdAsSeparateColumns() {
        JpaChatMemoryStore store = new JpaChatMemoryStore(repository);
        ChatMemoryId id = new ChatMemoryId("user-columns", "session-columns");

        store.updateMessages(id, List.of(new UserMessage("hi")));

        ChatMemoryEntity entity = repository.findByUserIdAndSessionId(id.userId(), id.sessionId()).orElseThrow();
        assertThat(entity.getUserId()).isEqualTo("user-columns");
        assertThat(entity.getSessionId()).isEqualTo("session-columns");
    }

    @Test
    void deleteMessagesRemovesMemory() {
        JpaChatMemoryStore store = new JpaChatMemoryStore(repository);
        ChatMemoryId id = new ChatMemoryId("user-delete", "session-delete");

        store.updateMessages(id, List.of(new UserMessage("hi")));
        store.deleteMessages(id);

        assertThat(store.getMessages(id)).isEmpty();
    }

    @Test
    void getMessagesReturnsEmptyForUnknownMemory() {
        JpaChatMemoryStore store = new JpaChatMemoryStore(repository);

        assertThat(store.getMessages(new ChatMemoryId("user-unknown", "session-unknown"))).isEmpty();
    }

}
