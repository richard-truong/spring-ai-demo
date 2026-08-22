package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.ProductSuggestion;

public record ProductSuggestionResponse(
    String name,
    String price,
    String description
) {

    public static ProductSuggestionResponse from(ProductSuggestion suggestion) {
        return new ProductSuggestionResponse(suggestion.name(), suggestion.price(), suggestion.description());
    }

}
