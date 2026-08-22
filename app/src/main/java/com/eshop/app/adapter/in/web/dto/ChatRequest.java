package com.eshop.app.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
    @NotBlank String sessionId,
    @NotBlank String message
) {
}
