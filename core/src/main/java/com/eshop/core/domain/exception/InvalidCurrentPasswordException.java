package com.eshop.core.domain.exception;

public class InvalidCurrentPasswordException extends DomainException {

    public InvalidCurrentPasswordException() {
        super("current password is incorrect");
    }

}
