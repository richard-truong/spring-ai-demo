package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.out.ProductSuggestionPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Component
@Profile("!langchain4j")
public class SpringAiProductSuggestionAdapter implements ProductSuggestionPort {

    private final ChatClient client;
    private final String systemPrompt;

    public SpringAiProductSuggestionAdapter(ChatClient.Builder builder,
                                            @Value("classpath:prompts/system.txt") Resource systemPromptResource) {
        this.client = builder.build();
        this.systemPrompt = readText(systemPromptResource);
    }

    @Override
    public ProductSuggestion suggest(ProductSuggestionCommand command) {
        String userMessage = "Product name: " + command.productName()
            + "\nPlatform: " + command.platform();

        return client.prompt()
            .system(systemPrompt)
            .user(userMessage)
            .call()
            .entity(ProductSuggestion.class);
    }

    private static String readText(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read system prompt from " + resource, e);
        }
    }

}
