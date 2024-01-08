package com.socialnet.controller;

import com.socialnet.annotation.AuthRequired;
import com.socialnet.annotation.FullSwaggerDescription;
import com.socialnet.dto.request.CommentRq;
import com.socialnet.dto.response.CommentRs;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.service.PostCommentsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.socialnet.annotation.Info;
import com.socialnet.annotation.OnlineStatusUpdate;

import java.util.List;

@Tag(name = "PostCommentsController", description = "Create, delete, read, edit and recover comments")
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@Info
public class PostCommentsController {
    private final PostCommentsService postCommentsService;

    @OnlineStatusUpdate
    @AuthRequired(summary = "edit comment by id")
    @PutMapping(value = "/{id}/comments/{comment_id}", produces = "application/json", consumes = "application/json")
    public CommonRs<CommentRs> editComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.editComment(authorization, id, commentId, commentRq);
    }

    @OnlineStatusUpdate
    @AuthRequired(summary = "delete comment by id")
    @DeleteMapping(value = "/{id}/comments/{comment_id}", produces = "application/json")
    public CommonRs<CommentRs> deleteComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.deleteComment(authorization, id, commentId);
    }

    @OnlineStatusUpdate
    @AuthRequired(summary = "recover comment by id")
    @PutMapping(value = "/{id}/comments/{comment_id}/recover", produces = "application/json")
    public CommonRs<CommentRs> recoverComment(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @PathVariable("comment_id") Long commentId
    ) {
        return postCommentsService.recoverComment(authorization, id, commentId);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "get comment by id")
    @GetMapping(value = "/{postId}/comments", produces = "application/json")
    public CommonRs<List<CommentRs>> getComments(
            @RequestHeader String authorization,
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postCommentsService.getComments(authorization, postId, offset, perPage);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "create comment")
    @PostMapping(value = "/{postId}/comments", produces = "application/json", consumes = "application/json")
    public CommonRs<CommentRs> createComment(
            @RequestHeader String authorization,
            @PathVariable Long postId,
            @RequestBody CommentRq commentRq
    ) {
        return postCommentsService.createComment(authorization, postId, commentRq);
    }
}
