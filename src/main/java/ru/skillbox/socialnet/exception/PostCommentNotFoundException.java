package ru.skillbox.socialnet.exception;

public class PostCommentNotFoundException extends BadRequestException {
    public PostCommentNotFoundException(Long commentId) {
        super("Post comment id " + commentId + " not found");
    }
}