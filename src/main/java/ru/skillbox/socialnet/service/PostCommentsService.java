package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.repository.PostCommentsRepository;

@Service
@RequiredArgsConstructor
public class PostCommentsService {
    private final PostCommentsRepository postCommentsRepository;

    public ResponseEntity<?> editComment(String authorization, Long id, Long commentId, CommentRq commentRq) {
        // TODO: PostCommentsService editComment
        return null;
    }

    public ResponseEntity<?> deleteComment(String authorization, Long id, Long commentId) {
        // TODO: PostCommentsService deleteComment
        return null;
    }

    public ResponseEntity<?> recoverComment(String authorization, Long id, Long commentId) {
        // TODO: PostCommentsService recoverComment
        return null;
    }

    public ResponseEntity<?> getComments(String authorization, Long postId, Integer offset, Integer perPage) {
        // TODO: PostCommentsService getComments
        return null;
    }

    public ResponseEntity<?> createComment(String authorization, Long postId, CommentRq commentRq) {
        // TODO: PostCommentsService createComment
        return null;
    }

    private long getMyId(String authorization) throws AuthenticationException {
        // TODO: PostCommentsService getMyId
        return 123l;
    }
}
