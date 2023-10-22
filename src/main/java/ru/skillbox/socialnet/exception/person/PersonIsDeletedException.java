package ru.skillbox.socialnet.exception.person;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PersonIsDeletedException extends BadRequestException {

    public PersonIsDeletedException(String message) {
        super(message);
    }

    public PersonIsDeletedException(Long personId) {
        super("Person id " + personId + " is deleted");
    }
}
