package com.socialnet.exception;

public class PersonIsDeletedException extends BadRequestException {

    public PersonIsDeletedException(String message) {
        super(message);
    }

    public PersonIsDeletedException(Long personId) {
        super("Person id " + personId + " is deleted");
    }
}
