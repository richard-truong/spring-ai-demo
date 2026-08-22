package com.eshop.core.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("invalid credentials");
    }

}
