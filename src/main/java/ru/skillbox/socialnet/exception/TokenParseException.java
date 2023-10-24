package ru.skillbox.socialnet.exception;

public class TokenParseException extends BadRequestException {
    public TokenParseException(String message) {
        super(message);
    }
}
