package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;
import com.eshop.core.application.port.out.ChatMemoryPort;
import com.eshop.core.domain.vo.ChatRole;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("langchain4j")
public class LangChain4jChatMemoryAdapter implements ChatMemoryPort {

    private final ChatAssistant assistant;
    private final ChatMemoryStore chatMemoryStore;

    public LangChain4jChatMemoryAdapter(ChatAssistant assistant, ChatMemoryStore chatMemoryStore) {
        this.assistant = assistant;
        this.chatMemoryStore = chatMemoryStore;
    }

    @Override
    public ChatReply reply(ChatMemoryId memoryId, String userMessage) {
        return new ChatReply(assistant.chat(memoryId, userMessage));
    }

    @Override
    public List<ChatMessage> history(ChatMemoryId memoryId) {
        return chatMemoryStore.getMessages(memoryId).stream()
            .filter(message -> message.type() == ChatMessageType.USER || message.type() == ChatMessageType.AI)
            .map(this::toDomain)
            .toList();
    }

    @Override
    public void clear(ChatMemoryId memoryId) {
        chatMemoryStore.deleteMessages(memoryId);
    }

    private ChatMessage toDomain(dev.langchain4j.data.message.ChatMessage message) {
        if (message.type() == ChatMessageType.USER) {
            return new ChatMessage(ChatRole.USER, ((UserMessage) message).singleText());
        }
        return new ChatMessage(ChatRole.AI, ((AiMessage) message).text());
    }

}
