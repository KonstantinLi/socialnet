package ru.skillbox.socialnet.exception.file;

import ru.skillbox.socialnet.exception.BadRequestException;

public class FileNotProvidedException extends BadRequestException {
    public FileNotProvidedException(String message) {
        super(message);
    }
}
