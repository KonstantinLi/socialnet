package ru.skillbox.socialnet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.dto.PostRq;
import ru.skillbox.socialnet.services.PostsService;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.getPostById(authorization, id);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updateById(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.updateById(authorization, id, postRq);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deleteById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.deleteById(authorization, id);
    }

    @PutMapping("/post/{id}/recover")
    public ResponseEntity<?> recoverPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.recoverPostById(authorization, id);
    }

    @GetMapping("/users/{id}/wall")
    public ResponseEntity<?> getWall(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getWall(authorization, id, offset, perPage);
    }

    @PostMapping("/users/{id}/wall")
    public ResponseEntity<?> createPost(
            @RequestParam String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.createPost(authorization, publishDate, id, postRq);
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
        return postsService.getPostsByQuery(
                authorization, author, dateFrom, dateTo, offset, perPage, tags, text
        );
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> getFeeds(
            @RequestParam String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getFeeds(authorization, offset, perPage);
    }
}
