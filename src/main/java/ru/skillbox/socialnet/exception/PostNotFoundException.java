package ru.skillbox.socialnet.exception;

public class PostNotFoundException extends BadRequestException {

    public PostNotFoundException(String message) {
        super(message);
    }
    public PostNotFoundException(Long postId) {
        super("Post id " + postId + " not found");
    }
}