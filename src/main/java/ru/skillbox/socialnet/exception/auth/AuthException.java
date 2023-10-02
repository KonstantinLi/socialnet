package ru.skillbox.socialnet.exception.auth;

import ru.skillbox.socialnet.exception.BadRequestException;

public class AuthException extends BadRequestException {
    public AuthException(String message) {
        super(message);
    }
}
