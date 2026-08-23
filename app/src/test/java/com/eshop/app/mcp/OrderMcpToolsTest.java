package com.eshop.app.mcp;

import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.domain.model.Order;
import org.junit.jupiter.api.Test;
import org.springaicommunity.mcp.annotation.McpTool;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderMcpToolsTest {

    @Test
    void getOrderReturnsOrderWhenFound() {
        OrderRepositoryPort port = mock(OrderRepositoryPort.class);
        Order order = mock(Order.class);
        when(port.findById("o1")).thenReturn(Optional.of(order));

        assertThat(new OrderMcpTools(port).getOrder("o1")).isSameAs(order);
    }

    @Test
    void getOrderReturnsNullWhenNotFound() {
        OrderRepositoryPort port = mock(OrderRepositoryPort.class);
        when(port.findById("o1")).thenReturn(Optional.empty());

        assertThat(new OrderMcpTools(port).getOrder("o1")).isNull();
    }

    @Test
    void getOrderIsAnMcpTool() throws NoSuchMethodException {
        Method method = OrderMcpTools.class.getMethod("getOrder", String.class);

        assertThat(method.getAnnotation(McpTool.class)).isNotNull();
    }

}
