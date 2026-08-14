package com.ridex.exception;

public class UserAccountInactiveException extends RuntimeException {

    public UserAccountInactiveException(String message) {
        super(message);
    }
}
