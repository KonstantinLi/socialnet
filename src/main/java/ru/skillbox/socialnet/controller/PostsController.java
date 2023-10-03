package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.service.PostsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostsController {

    private final JwtTokenUtils jwtTokenUtils;
    private final PostsService postsService;

    @GetMapping("/post/{id}")
    public CommonRs<PostRs> getPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.getPostById(authorization, id);
    }

    @PutMapping("/post/{id}")
    public CommonRs<PostRs> updateById(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.updateById(authorization, id, postRq);
    }

    @DeleteMapping("/post/{id}")
    public CommonRs<PostRs> deleteById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.deleteById(authorization, id);
    }

    @PutMapping("/post/{id}/recover")
    public CommonRs<PostRs> recoverPostById(
            @RequestHeader String authorization,
            @PathVariable Long id
    ) {
        return postsService.recoverPostById(authorization, id);
    }

    @GetMapping("/users/{id}/wall")
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getWall(authorization, id, offset, perPage);
    }

    @PostMapping("/users/{id}/wall")
    public CommonRs<PostRs> createPost(
            @RequestHeader String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.createPost(authorization, publishDate, id, postRq);
    }

    @GetMapping("/post")
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

    @GetMapping("/feeds")
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getFeeds(authorization, offset, perPage);
    }
}
