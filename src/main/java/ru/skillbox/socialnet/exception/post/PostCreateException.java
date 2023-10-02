package ru.skillbox.socialnet.exception.post;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PostCreateException extends BadRequestException {
    public PostCreateException(String message) {
        super(message);
    }
}