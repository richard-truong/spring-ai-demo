package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.TokenResult;

public record TokenResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {

    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(result.accessToken(), result.tokenType(), result.expiresIn());
    }

}
