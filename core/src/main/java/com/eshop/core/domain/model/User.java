package com.eshop.core.domain.model;

import com.eshop.core.domain.vo.Email;
import com.eshop.core.domain.vo.Role;

import java.time.Instant;
import java.util.Objects;

public record User(
    String id,
    Email email,
    String name,
    String passwordHash,
    Role role,
    Instant createdAt
) {

    public User {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

}
