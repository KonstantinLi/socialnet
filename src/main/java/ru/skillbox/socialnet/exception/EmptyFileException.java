package ru.skillbox.socialnet.exception;

public class EmptyFileException extends BadRequestException {
    public EmptyFileException(String message) {
        super(message);
    }
}
