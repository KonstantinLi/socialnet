package ru.skillbox.socialnet.errs;

public class BadRequestException extends Exception {

    public BadRequestException(String message) {
        super(message);
    }
}
