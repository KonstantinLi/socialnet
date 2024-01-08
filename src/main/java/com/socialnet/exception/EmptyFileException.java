package com.socialnet.exception;

public class EmptyFileException extends BadRequestException {
    public EmptyFileException(String message) {
        super(message);
    }
}
