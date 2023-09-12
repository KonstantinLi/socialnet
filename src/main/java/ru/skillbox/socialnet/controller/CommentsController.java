package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.CommentRq;
import ru.skillbox.socialnet.service.CommentsService;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentsService commentsService;

    @PutMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<?> editComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRq commentRq
    ) {
        return commentsService.editComment(authorization, id, commentId, commentRq);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return commentsService.deleteComment(authorization, id, commentId);
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    public ResponseEntity<?> recoverComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return commentsService.recoverComment(authorization, id, commentId);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(
            @RequestParam String authorization,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return commentsService.getComments(authorization, postId, offset, perPage);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> createComment(
            @RequestParam String authorization,
            @PathVariable Long postId,
            @RequestBody CommentRq commentRq
    ) {
        return commentsService.createComment(authorization, postId, commentRq);
    }
}
