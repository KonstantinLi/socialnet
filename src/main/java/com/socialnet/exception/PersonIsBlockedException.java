package com.socialnet.exception;

public class PersonIsBlockedException extends BadRequestException {
    public PersonIsBlockedException(String message) {
        super(message);
    }
}
