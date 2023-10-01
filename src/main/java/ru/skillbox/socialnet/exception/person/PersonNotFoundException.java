package ru.skillbox.socialnet.exception.person;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PersonNotFoundException extends BadRequestException {

    public PersonNotFoundException(String message) {
        super(message);
    }

    public PersonNotFoundException(Long personId) {
        super("Person id " + personId + " not found");
    }
}
