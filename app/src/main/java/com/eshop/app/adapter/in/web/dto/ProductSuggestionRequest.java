package com.eshop.app.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductSuggestionRequest(
    @NotBlank String productName,
    @NotBlank String platform
) {
}
