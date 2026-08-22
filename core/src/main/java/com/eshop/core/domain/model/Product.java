package com.eshop.core.domain.model;

import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.InsufficientStockException;
import com.eshop.core.domain.vo.Money;

import java.util.Objects;

public record Product(
    String id,
    String name,
    String description,
    Money price,
    int stock
) {

    public Product {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");
        if (name.isBlank()) {
            throw new DomainException("product name must not be blank");
        }
        if (price.isNegative()) {
            throw new DomainException("product price must not be negative");
        }
        if (stock < 0) {
            throw new DomainException("product stock must not be negative");
        }
    }

    public Product decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainException("quantity must be positive");
        }
        if (stock < quantity) {
            throw new InsufficientStockException(id);
        }
        return new Product(id, name, description, price, stock - quantity);
    }

}
