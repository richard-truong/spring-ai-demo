package com.eshop.core.domain.exception;

public class EmailAlreadyUsedException extends DomainException {

    public EmailAlreadyUsedException(String email) {
        super("email already registered");
    }

}
