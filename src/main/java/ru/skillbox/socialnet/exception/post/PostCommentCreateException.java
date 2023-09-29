package ru.skillbox.socialnet.exception.post;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PostCommentCreateException extends BadRequestException {
    public PostCommentCreateException(String message) {
        super(message);
    }
}