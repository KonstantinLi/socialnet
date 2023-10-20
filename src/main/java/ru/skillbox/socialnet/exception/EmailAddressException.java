package ru.skillbox.socialnet.exception;

public class EmailAddressException extends BadRequestException {
    public EmailAddressException(String message) {
        super(message);
    }
}
