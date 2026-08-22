package com.eshop.core.application.port.out;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;

import java.util.List;

public interface ChatMemoryPort {

    ChatReply reply(ChatMemoryId memoryId, String userMessage);

    List<ChatMessage> history(ChatMemoryId memoryId);

    void clear(ChatMemoryId memoryId);

}
