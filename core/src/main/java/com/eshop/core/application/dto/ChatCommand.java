package com.eshop.core.application.dto;

public record ChatCommand(
    String userId,
    String sessionId,
    String message
) {
}
