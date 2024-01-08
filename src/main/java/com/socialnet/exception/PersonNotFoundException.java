package com.socialnet.exception;

public class PersonNotFoundException extends BadRequestException {

    public PersonNotFoundException(String message) {
        super(message);
    }

    public PersonNotFoundException(Long personId) {
        super("Person id " + personId + " not found");
    }
}
