package ru.skillbox.socialnet.exception.post;

import ru.skillbox.socialnet.exception.BadRequestException;

public class PostCommentNotFoundException extends BadRequestException {
    public PostCommentNotFoundException(Long commentId) {
        super("Post comment id " + commentId + " not found");
    }
}