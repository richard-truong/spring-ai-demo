package com.eshop.core.application.dto;

public record ChangePasswordCommand(String userId, String currentPassword, String newPassword) {
}
