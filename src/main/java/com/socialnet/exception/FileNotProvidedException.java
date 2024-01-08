package com.socialnet.exception;

public class FileNotProvidedException extends BadRequestException {
    public FileNotProvidedException(String message) {
        super(message);
    }
}
