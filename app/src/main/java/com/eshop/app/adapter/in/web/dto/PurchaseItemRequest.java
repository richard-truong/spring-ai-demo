package com.eshop.app.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseItemRequest(
    @NotBlank String productId,
    @NotNull @Positive int quantity
) {
}
