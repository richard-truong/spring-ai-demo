package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.domain.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
    String id,
    String name,
    String description,
    PriceResponse price,
    int stock
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.id(),
            product.name(),
            product.description(),
            new PriceResponse(product.price().amount(), product.price().currency()),
            product.stock()
        );
    }

    public record PriceResponse(BigDecimal amount, String currency) {
    }

}
