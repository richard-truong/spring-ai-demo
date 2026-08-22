package com.eshop.core.domain.model;

import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.vo.Money;

import java.util.Objects;

public record OrderItem(
    String productId,
    String name,
    int quantity,
    Money unitPrice
) {

    public OrderItem {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (quantity <= 0) {
            throw new DomainException("quantity must be positive");
        }
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }

}
