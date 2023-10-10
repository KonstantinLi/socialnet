package ru.skillbox.socialnet.exception.person;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PersonIsBlockedException extends BadRequestException {
    public PersonIsBlockedException(String message) {
        super(message);
    }
}
