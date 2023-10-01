package ru.skillbox.socialnet.exception.file;

import ru.skillbox.socialnet.exception.BadRequestException;

public class EmptyFileException extends BadRequestException {
    public EmptyFileException(String message) {
        super(message);
    }
}
