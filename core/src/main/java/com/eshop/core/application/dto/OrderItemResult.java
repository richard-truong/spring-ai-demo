package com.eshop.core.application.dto;

import com.eshop.core.domain.vo.Money;

public record OrderItemResult(
    String productId,
    String name,
    int quantity,
    Money unitPrice,
    Money subtotal
) {
}
