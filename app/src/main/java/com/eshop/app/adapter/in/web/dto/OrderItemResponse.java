package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.OrderItemResult;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productId,
    String name,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {

    public static OrderItemResponse from(OrderItemResult result) {
        return new OrderItemResponse(
            result.productId(),
            result.name(),
            result.quantity(),
            result.unitPrice().amount(),
            result.subtotal().amount()
        );
    }

}
