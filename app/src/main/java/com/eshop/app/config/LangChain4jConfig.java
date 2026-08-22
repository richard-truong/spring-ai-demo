package com.eshop.app.config;

import com.eshop.app.adapter.out.ai.ChatAssistant;
import com.eshop.app.adapter.out.ai.ProductSuggestionAssistant;
import com.eshop.app.memory.ChatMemoryJpaRepository;
import com.eshop.app.memory.JpaChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiTokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public ChatMemoryStore chatMemoryStore(ChatMemoryJpaRepository repository) {
        return new JpaChatMemoryStore(repository);
    }

    @Bean
    @Profile("openai")
    public TokenCountEstimator openAiTokenCountEstimator(
            @Value("${langchain4j.open-ai.chat-model.model-name:gpt-4o-mini}") String modelName) {
        return new OpenAiTokenCountEstimator(modelName);
    }

    @Bean
    @Profile("gemini")
    public TokenCountEstimator geminiTokenCountEstimator(
            @Value("${langchain4j.google-ai-gemini.chat-model.model-name:gemini-2.5-flash}") String modelName,
            @Value("${langchain4j.google-ai-gemini.chat-model.api-key:}") String apiKey) {
        return GoogleAiGeminiTokenCountEstimator.builder()
            .modelName(modelName)
            .apiKey(apiKey)
            .build();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatMemoryStore chatMemoryStore,
                                                 TokenCountEstimator tokenCountEstimator,
                                                 @Value("${langchain4j.chat-memory.max-tokens:2000}") int maxTokens) {
        return memoryId -> TokenWindowChatMemory.builder()
            .id(memoryId)
            .maxTokens(maxTokens, tokenCountEstimator)
            .chatMemoryStore(chatMemoryStore)
            .build();
    }

    @Bean
    public ChatAssistant chatAssistant(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(ChatAssistant.class)
            .chatModel(chatModel)
            .chatMemoryProvider(chatMemoryProvider)
            .build();
    }

}
