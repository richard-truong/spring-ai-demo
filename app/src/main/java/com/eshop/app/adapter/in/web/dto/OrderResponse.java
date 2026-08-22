package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.OrderResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    String id,
    String userId,
    List<OrderItemResponse> items,
    BigDecimal total,
    String status,
    Instant createdAt
) {

    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(
            result.id(),
            result.userId(),
            result.items().stream().map(OrderItemResponse::from).toList(),
            result.total().amount(),
            result.status().name(),
            result.createdAt()
        );
    }

}
