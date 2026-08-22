package com.eshop.app.memory;

import com.eshop.core.application.dto.ChatMemoryId;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.List;

public class JpaChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryJpaRepository repository;

    public JpaChatMemoryStore(ChatMemoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        ChatMemoryId id = (ChatMemoryId) memoryId;
        return repository.findByUserIdAndSessionId(id.userId(), id.sessionId())
            .map(entity -> ChatMessageDeserializer.messagesFromJson(entity.getMessages()))
            .orElseGet(List::of);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        ChatMemoryId id = (ChatMemoryId) memoryId;
        String json = ChatMessageSerializer.messagesToJson(messages);
        ChatMemoryEntity entity = repository.findByUserIdAndSessionId(id.userId(), id.sessionId())
            .map(existing -> existing.withMessages(json))
            .orElseGet(() -> new ChatMemoryEntity(id.userId(), id.sessionId(), json));
        repository.save(entity);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        ChatMemoryId id = (ChatMemoryId) memoryId;
        repository.findByUserIdAndSessionId(id.userId(), id.sessionId())
            .ifPresent(repository::delete);
    }

}
