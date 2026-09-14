package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;
import com.eshop.core.application.port.in.ChatUseCase;
import com.eshop.core.application.port.out.ChatMemoryPort;

import java.util.List;

public class ChatUseCaseImpl implements ChatUseCase {

    private final ChatMemoryPort chatMemoryPort;

    public ChatUseCaseImpl(ChatMemoryPort chatMemoryPort) {
        this.chatMemoryPort = chatMemoryPort;
    }

    @Override
    public ChatReply reply(ChatCommand command) {
        return chatMemoryPort.reply(memoryId(command.userId(), command.sessionId()), command.message());
    }

    @Override
    public List<ChatMessage> history(String userId, String sessionId) {
        return chatMemoryPort.history(memoryId(userId, sessionId));
    }

    @Override
    public void clear(String userId, String sessionId) {
        chatMemoryPort.clear(memoryId(userId, sessionId));
    }

    private ChatMemoryId memoryId(String userId, String sessionId) {
        return new ChatMemoryId(userId, sessionId);
    }

}
