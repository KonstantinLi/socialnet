package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommonRsListPostRs;
import ru.skillbox.socialnet.dto.response.CommonRsPostRs;
import ru.skillbox.socialnet.service.PostsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PostsController {

    private final JwtTokenUtils jwtTokenUtils;
    private final PostsService postsService;

    @GetMapping("/post/{id}")
    public CommonRsPostRs getPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.getPostById(authorization, id);
    }

    @PutMapping("/post/{id}")
    public CommonRsPostRs updateById(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.updateById(authorization, id, postRq);
    }

    @DeleteMapping("/post/{id}")
    public CommonRsPostRs deleteById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.deleteById(authorization, id);
    }

    @PutMapping("/post/{id}/recover")
    public CommonRsPostRs recoverPostById(
            @RequestParam String authorization,
            @PathVariable Long id
    ) {
        return postsService.recoverPostById(authorization, id);
    }

    @GetMapping("/users/{id}/wall")
    public CommonRsListPostRs getWall(
            @RequestParam String authorization,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getWall(authorization, id, offset, perPage);
    }

    @PostMapping("/users/{id}/wall")
    public CommonRsPostRs createPost(
            @RequestParam String authorization,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @PathVariable Long id,
            @RequestBody PostRq postRq
    ) {
        return postsService.createPost(authorization, publishDate, id, postRq);
    }

    @GetMapping("post")
    public CommonRs<List<PostRs>> getPostsByQuery(@RequestHeader(value = "authorization") String token,
            @RequestParam(required = false) String author,
            @RequestParam(value = "date_from", defaultValue = "0") Long dateFrom,
            @RequestParam(value = "date_to", defaultValue = "0") Long dateTo,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage,
            @RequestParam(required = false) String[] tags,
            @RequestParam(required = false) String text
    ) {
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
    public CommonRsListPostRs getFeeds(
            @RequestParam String authorization,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "20") Integer perPage
    ) {
        return postsService.getFeeds(authorization, offset, perPage);
    }
}
