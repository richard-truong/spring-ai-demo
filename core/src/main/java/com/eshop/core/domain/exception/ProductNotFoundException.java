package com.eshop.core.domain.exception;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(String productId) {
        super("product not found");
    }

}
