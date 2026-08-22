package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringAiProductSuggestionAdapterTest {

    @Test
    void parsesModelJsonResponseIntoProductSuggestion() {
        ChatModel chatModel = mock(ChatModel.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(
            new AssistantMessage("{\"name\":\"Laptop Gaming X1\",\"price\":\"1.999.000đ\",\"description\":\"RTX 4060, 16GB RAM\"}")))));

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        SpringAiProductSuggestionAdapter adapter = new SpringAiProductSuggestionAdapter(
            builder, new ClassPathResource("prompts/system.txt"));

        ProductSuggestion result = adapter.suggest(new ProductSuggestionCommand("Laptop", "shopee"));

        assertThat(result.name()).isEqualTo("Laptop Gaming X1");
        assertThat(result.price()).isEqualTo("1.999.000đ");
        assertThat(result.description()).isEqualTo("RTX 4060, 16GB RAM");
    }

}
