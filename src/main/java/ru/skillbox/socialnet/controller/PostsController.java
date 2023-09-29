package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.PostRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.service.GetPostsSearchPs;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.service.PostsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PostsController {

    private final JwtTokenUtils jwtTokenUtils;
    private final PostsService postsService;

    @GetMapping("post")
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
}
