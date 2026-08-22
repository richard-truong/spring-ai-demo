package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ProductSuggestion;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ProductSuggestionAssistant {

    @SystemMessage(fromResource = "prompts/system.txt")
    @UserMessage("{{it}}")
    ProductSuggestion suggest(String userMessage);

}
