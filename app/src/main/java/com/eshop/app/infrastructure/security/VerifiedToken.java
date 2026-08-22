package com.eshop.app.infrastructure.security;

import com.eshop.core.domain.vo.Role;

public record VerifiedToken(String userId, String email, Role role) {
}
