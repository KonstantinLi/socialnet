package com.socialnet.exception;

public class FileSizeException extends BadRequestException {
    public FileSizeException(String message) {
        super(message);
    }
}
