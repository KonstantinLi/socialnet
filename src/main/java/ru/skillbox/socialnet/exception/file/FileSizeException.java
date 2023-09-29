package ru.skillbox.socialnet.exception.file;

import ru.skillbox.socialnet.exception.BadRequestException;

public class FileSizeException extends BadRequestException {
    public FileSizeException(String message) {
        super(message);
    }
}
