package com.eshop.core.domain.model;

import com.eshop.core.domain.exception.EmptyOrderException;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record Order(
    String id,
    String userId,
    List<OrderItem> items,
    OrderStatus status,
    Instant createdAt
) {

    public Order {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException();
        }
        items = List.copyOf(items);
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    public Money total() {
        return items.stream()
            .map(OrderItem::subtotal)
            .reduce(Money::add)
            .orElseThrow(EmptyOrderException::new);
    }

}
