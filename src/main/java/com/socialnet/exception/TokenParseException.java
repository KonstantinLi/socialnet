package com.socialnet.exception;

public class TokenParseException extends BadRequestException {
    public TokenParseException(String message) {
        super(message);
    }
}
