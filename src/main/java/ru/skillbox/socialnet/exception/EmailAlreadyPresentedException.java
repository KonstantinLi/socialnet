package ru.skillbox.socialnet.exception;

public class EmailAlreadyPresentedException extends CommonException {
    public EmailAlreadyPresentedException(String message) {
        super(message);
    }
}
