package com.socialnet.exception;

public class PostCreateException extends BadRequestException {
    public PostCreateException(String message) {
        super(message);
    }
}