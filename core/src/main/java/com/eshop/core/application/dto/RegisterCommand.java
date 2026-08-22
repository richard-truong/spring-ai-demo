package com.eshop.core.application.dto;

public record RegisterCommand(
    String email,
    String password,
    String name
) {
}
