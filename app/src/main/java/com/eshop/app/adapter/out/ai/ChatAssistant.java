package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistant {

    @SystemMessage("""
        You are a helpful shopping assistant for EvShop.
        When reference information is provided, answer using only that information; if it does not
        contain the answer, say so instead of guessing.
        Otherwise answer concisely and helpfully from your general knowledge.
        """)
    String chat(@MemoryId ChatMemoryId memoryId, @UserMessage String message);

}
