package ru.skillbox.socialnet.service;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.dto.request.CommentRq;

@Service
public class CommentsService {
    public ResponseEntity<?> editComment(String authorization, Long id, Long commentId, CommentRq commentRq) {
        // TODO: CommentsService editComment
        return null;
    }

    public ResponseEntity<?> deleteComment(String authorization, Long id, Long commentId) {
        // TODO: CommentsService deleteComment
        return null;
    }

    public ResponseEntity<?> recoverComment(String authorization, Long id, Long commentId) {
        // TODO: CommentsService recoverComment
        return null;
    }

    public ResponseEntity<?> getComments(String authorization, Long postId, Integer offset, Integer perPage) {
        // TODO: CommentsService getComments
        return null;
    }

    public ResponseEntity<?> createComment(String authorization, Long postId, CommentRq commentRq) {
        // TODO: CommentsService createComment
        return null;
    }
}
