package com.eshop.core.domain.exception;

public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException() {
        super("all order items must use the same currency");
    }

}
