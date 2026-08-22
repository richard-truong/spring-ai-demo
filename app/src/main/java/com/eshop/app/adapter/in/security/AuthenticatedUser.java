package com.eshop.app.adapter.in.security;

import com.eshop.core.domain.vo.Role;

public record AuthenticatedUser(String id, String email, Role role) {
}
