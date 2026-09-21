package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.DomainException;

import java.nio.charset.StandardCharsets;

public record Password(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_BYTES = 72;

    public Password {
        if (value == null || value.isBlank()) {
            throw new DomainException("password must not be blank");
        }
        if (value.length() < MIN_LENGTH) {
            throw new DomainException("password must be between 8 and 72 characters");
        }
        if (value.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new DomainException("password must be at most 72 bytes");
        }
    }

}
