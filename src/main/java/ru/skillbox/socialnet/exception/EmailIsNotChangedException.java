package ru.skillbox.socialnet.exception;

public class EmailIsNotChangedException extends BadRequestException {

    public EmailIsNotChangedException(String message) {
        super(message);
    }
}
