package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LangChain4jProductSuggestionAdapterTest {

    @Test
    void delegatesToAssistantWithFormattedMessage() {
        ProductSuggestionAssistant assistant = mock(ProductSuggestionAssistant.class);
        when(assistant.suggest("Product name: Laptop\nPlatform: shopee"))
            .thenReturn(new ProductSuggestion("Laptop X", "1000", "desc"));

        LangChain4jProductSuggestionAdapter adapter = new LangChain4jProductSuggestionAdapter(assistant);

        ProductSuggestion result = adapter.suggest(new ProductSuggestionCommand("Laptop", "shopee"));

        assertThat(result.name()).isEqualTo("Laptop X");
        assertThat(result.price()).isEqualTo("1000");
        assertThat(result.description()).isEqualTo("desc");
    }

}
