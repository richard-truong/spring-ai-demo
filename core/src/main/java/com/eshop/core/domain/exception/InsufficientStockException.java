package com.eshop.core.domain.exception;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String productId) {
        super("insufficient stock");
    }

}
