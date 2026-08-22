package com.eshop.core.application.dto;

import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResult(
    String id,
    String userId,
    List<OrderItemResult> items,
    Money total,
    OrderStatus status,
    Instant createdAt
) {
}
