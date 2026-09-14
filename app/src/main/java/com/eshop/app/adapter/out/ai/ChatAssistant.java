package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistant {

    @SystemMessage("You are a helpful shopping assistant for EvShop. Answer concisely and helpfully.")
    String chat(@MemoryId ChatMemoryId memoryId, @UserMessage String message);

}
