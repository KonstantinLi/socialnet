package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommonRsListPostRs;
import ru.skillbox.socialnet.dto.response.CommonRsPostRs;
import ru.skillbox.socialnet.service.PostsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @GetMapping("/post/{id}")
    public CommonRsPostRs getPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.getPostById(authorization, id);
    }

    @PutMapping("/post/{id}")
    public CommonRsPostRs updateById(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.updateById(authorization, id, postRq);
    }

    @DeleteMapping("/post/{id}")
    public CommonRsPostRs deleteById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.deleteById(authorization, id);
    }

    @PutMapping("/post/{id}/recover")
    public CommonRsPostRs recoverPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.recoverPostById(authorization, id);
    }

    @GetMapping("/users/{id}/wall")
    public CommonRsListPostRs getWall(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getWall(authorization, id, offset, perPage);
    }

    @PostMapping("/users/{id}/wall")
    public CommonRsPostRs createPost(
            @RequestHeader String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.createPost(authorization, publishDate, id, postRq);
    }

    @GetMapping("/post")
    public CommonRsListPostRs getPostsByQuery(
            @RequestHeader String authorization,
            @RequestParam(required = false) String author,
            @RequestParam(value = "date_from", required = false) Long dateFrom,
            @RequestParam(value = "date_to", required = false) Long dateTo,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) String text
    ) {
        return postsService.getPostsByQuery(
                authorization, author, dateFrom, dateTo, offset, perPage, tags, text
        );
    }

    @GetMapping("/feeds")
    public CommonRsListPostRs getFeeds(
            @RequestHeader String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getFeeds(authorization, offset, perPage);
    }
}
