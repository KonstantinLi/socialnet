package ru.skillbox.socialnet.exception.auth;

import ru.skillbox.socialnet.exception.BadRequestException;

public class ValidationException extends BadRequestException {

    public ValidationException(String message) {
        super(message);
    }

}
