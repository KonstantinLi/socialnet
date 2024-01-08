package com.socialnet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.socialnet.annotation.AuthRequired;
import com.socialnet.annotation.FullSwaggerDescription;
import com.socialnet.annotation.Info;
import com.socialnet.annotation.OnlineStatusUpdate;
import com.socialnet.dto.parameters.GetPostsSearchPs;
import com.socialnet.dto.request.PostRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.PostRs;
import com.socialnet.security.JwtTokenUtils;
import com.socialnet.service.PostsService;

import java.util.List;

@Tag(name = "PostsController", description = "Get feeds. Get, update, delete, recover, find post, get users post, create post")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Info
public class PostsController {

    private final JwtTokenUtils jwtTokenUtils;
    private final PostsService postsService;

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "get post by id")
    @GetMapping(value = "/post/{id}", produces = "application/json")
    public CommonRs<PostRs> getPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.getPostById(authorization, id);
    }

    @OnlineStatusUpdate
    @AuthRequired(summary = "update post by id")
    @PutMapping(value = "/post/{id}", produces = "application/json", consumes = "application/json")
    public CommonRs<PostRs> updateById(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.updateById(authorization, id, postRq);
    }

    @OnlineStatusUpdate
    @AuthRequired(summary = "delete post by id")
    @DeleteMapping(value = "/post/{id}", produces = "application/json")
    public CommonRs<PostRs> deleteById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.deleteById(authorization, id);
    }

    @OnlineStatusUpdate
    @AuthRequired(summary = "recover post by id")
    @PutMapping(value = "/post/{id}/recover", produces = "application/json")
    public CommonRs<PostRs> recoverPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.recoverPostById(authorization, id);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "get all post by author id")
    @GetMapping(value = "/users/{id}/wall", produces = "application/json")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getWall(authorization, id, offset, perPage);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "create new post")
    @PostMapping(value = "/users/{id}/wall", produces = "application/json", consumes = "application/json")
    public CommonRs<PostRs> createPost(
            @RequestHeader String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.createPost(authorization, publishDate, id, postRq);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "get posts by query")
    @GetMapping(value = "/post", produces = "application/json")
    public CommonRs<List<PostRs>> getPostsByQuery(@RequestHeader(value = "authorization") String token,
                                                  @RequestParam(value = "author", required = false) String author,
                                                  @RequestParam(value = "date_from",
                                                          required = false, defaultValue = "0") long dateFrom,
                                                  @RequestParam(value = "date_to",
                                                          required = false, defaultValue = "0") long dateTo,
                                                  @RequestParam(value = "offset",
                                                          required = false, defaultValue = "0") int offset,
                                                  @RequestParam(value = "perPage",
                                                          required = false, defaultValue = "20") int perPage,
                                                  @RequestParam(value = "tags", required = false) String[] tags,
                                                  @RequestParam(value = "text", required = false) String text) {
        return postsService.getPostsByQuery(jwtTokenUtils.getId(token),
                GetPostsSearchPs.builder()
                        .author(author)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .tags(tags)
                        .text(text).build(),
                offset,
                perPage);
    }

    @FullSwaggerDescription(summary = "get all news")
    @GetMapping(value = "/feeds", produces = "application/json")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getFeeds(authorization, offset, perPage);
    }
}
