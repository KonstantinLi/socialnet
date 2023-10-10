package ru.skillbox.socialnet.exception.file;

import ru.skillbox.socialnet.exception.BadRequestException;

public class UnsupportedFileTypeException extends BadRequestException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
