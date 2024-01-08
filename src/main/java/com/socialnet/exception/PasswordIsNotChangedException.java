package com.socialnet.exception;

public class PasswordIsNotChangedException extends BadRequestException {
    public PasswordIsNotChangedException(String message) {
        super(message);
    }
}
