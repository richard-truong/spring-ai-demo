package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.DomainException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern PATTERN =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new DomainException("email must not be blank");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new DomainException("email must be well-formed");
        }
        value = normalized;
    }

}
