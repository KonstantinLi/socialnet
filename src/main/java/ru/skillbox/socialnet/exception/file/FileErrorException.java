package ru.skillbox.socialnet.exception.file;

import ru.skillbox.socialnet.exception.BadRequestException;

public class FileErrorException extends BadRequestException {
    public FileErrorException(String message) {
        super(message);
    }
}
