package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.OnlineStatusUpdate;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.service.PostCommentsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostCommentsController {
    private final PostCommentsService postCommentsService;

    @OnlineStatusUpdate
    @PutMapping("/{id}/comments/{comment_id}")
    public CommonRs<CommentRs> editComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.editComment(authorization, id, commentId, commentRq);
    }

    @OnlineStatusUpdate
    @DeleteMapping("/{id}/comments/{comment_id}")
    public CommonRs<CommentRs> deleteComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.deleteComment(authorization, id, commentId);
    }

    @OnlineStatusUpdate
    @PutMapping("/{id}/comments/{comment_id}/recover")
    public CommonRs<CommentRs> recoverComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.recoverComment(authorization, id, commentId);
    }

    @OnlineStatusUpdate
    @GetMapping("/{postId}/comments")
    public CommonRs<List<CommentRs>> getComments(
            @RequestHeader String authorization,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postCommentsService.getComments(authorization, postId, offset, perPage);
    }

    @OnlineStatusUpdate
    @PostMapping("/{postId}/comments")
    public CommonRs<CommentRs> createComment(
            @RequestHeader String authorization,
            @PathVariable Long postId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.createComment(authorization, postId, commentRq);
    }
}
