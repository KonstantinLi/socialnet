package com.socialnet.exception;

public class UnsupportedFileTypeException extends BadRequestException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
