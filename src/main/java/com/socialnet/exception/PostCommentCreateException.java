package com.socialnet.exception;

public class PostCommentCreateException extends BadRequestException {
    public PostCommentCreateException(String message) {
        super(message);
    }
}