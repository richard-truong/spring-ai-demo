package com.eshop.app.mcp;

import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springaicommunity.mcp.annotation.McpTool;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductMcpToolsTest {

    @Test
    void getProductReturnsProductWhenFound() {
        ProductRepositoryPort port = mock(ProductRepositoryPort.class);
        Product product = mock(Product.class);
        when(port.findById("p1")).thenReturn(Optional.of(product));

        assertThat(new ProductMcpTools(port).getProduct("p1")).isSameAs(product);
    }

    @Test
    void getProductReturnsNullWhenNotFound() {
        ProductRepositoryPort port = mock(ProductRepositoryPort.class);
        when(port.findById("p1")).thenReturn(Optional.empty());

        assertThat(new ProductMcpTools(port).getProduct("p1")).isNull();
    }

    @Test
    void getProductIsAnMcpTool() throws NoSuchMethodException {
        Method method = ProductMcpTools.class.getMethod("getProduct", String.class);

        assertThat(method.getAnnotation(McpTool.class)).isNotNull();
    }

}
