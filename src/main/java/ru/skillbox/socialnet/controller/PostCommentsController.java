package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.CommonRsCommentRs;
import ru.skillbox.socialnet.dto.response.CommonRsListCommentRs;
import ru.skillbox.socialnet.service.PostCommentsService;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostCommentsController {
    private final PostCommentsService postCommentsService;

    @PutMapping("/{id}/comments/{comment_id}")
    public CommonRsCommentRs editComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.editComment(authorization, id, commentId, commentRq);
    }

    @DeleteMapping("/{id}/comments/{comment_id}")
    public CommonRsCommentRs deleteComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.deleteComment(authorization, id, commentId);
    }

    @PutMapping("/{id}/comments/{comment_id}/recover")
    public CommonRsCommentRs recoverComment(
            @RequestParam String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.recoverComment(authorization, id, commentId);
    }

    @GetMapping("/{postId}/comments")
    public CommonRsListCommentRs getComments(
            @RequestParam String authorization,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postCommentsService.getComments(authorization, postId, offset, perPage);
    }

    @PostMapping("/{postId}/comments")
    public CommonRsCommentRs createComment(
            @RequestParam String authorization,
            @PathVariable Long postId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.createComment(authorization, postId, commentRq);
    }
}
