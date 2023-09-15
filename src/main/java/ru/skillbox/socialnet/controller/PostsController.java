package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.service.post.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostsController {
    private final GetPostByIdService getPostByIdService;
    private final UpdatePostByIdService updatePostByIdService;
    private final DeletePostByIdService deletePostByIdService;
    private final RecoverPostByIdService recoverPostByIdService;
    private final GetPostWallService getPostWallService;
    private final CreatePostService createPostService;
    private final GetPostsByQueryService getPostsByQueryService;
    private final GetFeedsService getFeedsService;

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return getPostByIdService.getPostById(authorization, id);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updateById(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return updatePostByIdService.updateById(authorization, id, postRq);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deleteById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return deletePostByIdService.deleteById(authorization, id);
    }

    @PutMapping("/post/{id}/recover")
    public ResponseEntity<?> recoverPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return recoverPostByIdService.recoverPostById(authorization, id);
    }

    @GetMapping("/users/{id}/wall")
    public ResponseEntity<?> getWall(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return getPostWallService.getWall(authorization, id, offset, perPage);
    }

    @PostMapping("/users/{id}/wall")
    public ResponseEntity<?> createPost(
            @RequestParam String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return createPostService.createPost(authorization, publishDate, id, postRq);
    }

    @GetMapping("/post")
    public ResponseEntity<?> getPostsByQuery(
            @RequestParam String authorization,
            @RequestParam(defaultValue = "") String author,
            @RequestParam(value = "date_from", defaultValue = "0") Long dateFrom,
            @RequestParam(value = "date_to", defaultValue = "0") Long dateTo,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "") String text
    ) {
        return getPostsByQueryService.getPostsByQuery(
                authorization, author, dateFrom, dateTo, offset, perPage, tags, text
        );
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> getFeeds(
            @RequestParam String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return getFeedsService.getFeeds(authorization, offset, perPage);
    }
}
