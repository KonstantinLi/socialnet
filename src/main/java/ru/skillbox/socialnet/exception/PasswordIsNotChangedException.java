package ru.skillbox.socialnet.exception;

public class PasswordIsNotChangedException extends BadRequestException {
    public PasswordIsNotChangedException(String message) {
        super(message);
    }
}
