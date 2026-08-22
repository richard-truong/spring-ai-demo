package com.eshop.core.application.dto;

public record LoginCommand(
    String email,
    String password
) {
}
