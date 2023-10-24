package ru.skillbox.socialnet.exception;

public class PostCreateException extends BadRequestException {
    public PostCreateException(String message) {
        super(message);
    }
}