package com.eshop.app.mcp;

import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.domain.model.Order;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderMcpTools {

    private final OrderRepositoryPort orderRepository;

    public OrderMcpTools(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @McpTool(name = "getOrder", description = "Get an order by its id from the EvShop database")
    public Order getOrder(@McpToolParam(description = "The order id") String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

}
