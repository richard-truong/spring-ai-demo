package com.eshop.app.config;

import com.eshop.app.adapter.out.ai.ProductSuggestionAssistant;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("langchain4j")
public class LangChain4jConfig {

    @Bean
    public ProductSuggestionAssistant productSuggestionAssistant(ChatModel chatModel) {
        return AiServices.builder(ProductSuggestionAssistant.class)
            .chatModel(chatModel)
            .build();
    }

}
