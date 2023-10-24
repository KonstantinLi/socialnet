package ru.skillbox.socialnet.exception;

public class EmailContentException extends BadRequestException {
    public EmailContentException(String message) {
        super(message);
    }
}
