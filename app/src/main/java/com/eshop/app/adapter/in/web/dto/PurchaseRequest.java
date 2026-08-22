package com.eshop.app.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PurchaseRequest(
    @NotEmpty List<@Valid PurchaseItemRequest> items
) {
}
