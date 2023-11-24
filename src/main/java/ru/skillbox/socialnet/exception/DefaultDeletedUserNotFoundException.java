package ru.skillbox.socialnet.exception;

public class DefaultDeletedUserNotFoundException extends BadRequestException {
    public DefaultDeletedUserNotFoundException(String message) {
        super(message);
    }
}
