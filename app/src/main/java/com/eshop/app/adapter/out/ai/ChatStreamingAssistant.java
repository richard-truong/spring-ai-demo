package com.eshop.app.adapter.out.ai;

import com.eshop.app.config.AssistantPrompts;
import com.eshop.core.application.dto.ChatMemoryId;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface ChatStreamingAssistant {

    @SystemMessage(AssistantPrompts.CHAT_SYSTEM_MESSAGE)
    TokenStream chat(@MemoryId ChatMemoryId memoryId, @UserMessage String message);

}
