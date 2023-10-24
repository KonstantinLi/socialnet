package ru.skillbox.socialnet.exception;

public class AuthException extends BadRequestException {
    public AuthException(String message) {
        super(message);
    }
}
