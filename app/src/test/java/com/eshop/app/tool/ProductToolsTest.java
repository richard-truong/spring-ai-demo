package com.eshop.app.tool;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.out.ProductSuggestionPort;
import dev.langchain4j.agent.tool.Tool;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductToolsTest {

    @Test
    void searchDelegatesToPort() {
        ProductSuggestionPort port = mock(ProductSuggestionPort.class);
        ProductSuggestion expected = new ProductSuggestion("Laptop Gaming X1", "19.990.000đ", "RTX 4060");
        when(port.suggest(new ProductSuggestionCommand("Espresso", "shopee"))).thenReturn(expected);

        ProductTools tools = new ProductTools(port);

        ProductSuggestion result = tools.searchProductOnPlatform("Espresso", "shopee");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void searchIsAnAnnotatedTool() throws NoSuchMethodException {
        Method method = ProductTools.class.getMethod("searchProductOnPlatform", String.class, String.class);

        assertThat(method.getAnnotation(Tool.class)).isNotNull();
    }

}
