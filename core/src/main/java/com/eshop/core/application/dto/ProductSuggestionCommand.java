package com.eshop.core.application.dto;

public record ProductSuggestionCommand(
    String productName,
    String platform
) {
}
