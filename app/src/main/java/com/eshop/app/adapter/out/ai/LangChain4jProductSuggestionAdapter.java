package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.out.ProductSuggestionPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("langchain4j")
public class LangChain4jProductSuggestionAdapter implements ProductSuggestionPort {

    private final ProductSuggestionAssistant assistant;

    public LangChain4jProductSuggestionAdapter(ProductSuggestionAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public ProductSuggestion suggest(ProductSuggestionCommand command) {
        String userMessage = "Product name: " + command.productName()
            + "\nPlatform: " + command.platform();
        return assistant.suggest(userMessage);
    }

}
