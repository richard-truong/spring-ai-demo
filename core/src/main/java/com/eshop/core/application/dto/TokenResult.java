package com.eshop.core.application.dto;

public record TokenResult(
    String accessToken,
    String tokenType,
    long expiresIn
) {
}
