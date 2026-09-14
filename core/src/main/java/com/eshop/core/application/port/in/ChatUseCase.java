package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;

import java.util.List;

public interface ChatUseCase {

    ChatReply reply(ChatCommand command);

    List<ChatMessage> history(String userId, String sessionId);

    void clear(String userId, String sessionId);

}
