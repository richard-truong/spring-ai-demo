package com.eshop.core.domain.vo;

import com.eshop.core.domain.exception.CurrencyMismatchException;
import com.eshop.core.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public static final int SCALE = 2;

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        String normalized = currency.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() != 3) {
            throw new DomainException("currency must be a three-letter ISO 4217 code");
        }
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException e) {
            throw new DomainException("currency must be a supported ISO 4217 code");
        }
        amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        currency = normalized;
    }

    public Money(String amount, String currency) {
        this(new BigDecimal(amount), currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be non-negative");
        }
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException();
        }
    }

}
