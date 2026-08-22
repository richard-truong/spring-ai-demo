package com.eshop.core.domain.exception;

public class EmptyOrderException extends DomainException {

    public EmptyOrderException() {
        super("order must contain at least one item");
    }

}
