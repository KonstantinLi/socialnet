package ru.skillbox.socialnet.exception.post;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PostNotFoundException extends BadRequestException {
    public PostNotFoundException(Long postId) {
        super("Post id " + postId + " not found");
    }
}