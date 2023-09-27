package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.LikeRq;
import ru.skillbox.socialnet.dto.response.CommonRsLikeRs;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.service.LikesService;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @GetMapping
    public CommonRsLikeRs getLikes(
            @RequestParam String authorization,
            @RequestParam("item_id") Long itemId,
            @RequestParam LikeType type
    ) {
        return likesService.getLikes(authorization, itemId, type);
    }

    @PutMapping
    public CommonRsLikeRs putLike(
            @RequestParam String authorization,
            @RequestBody LikeRq likeRq
    ) {
        return likesService.putLike(authorization, likeRq);
    }

    @DeleteMapping
    public CommonRsLikeRs deleteLike(
            @RequestParam String authorization,
            @RequestParam("item_id") Long itemId,
            @RequestParam LikeType type
    ) {
        return likesService.deleteLike(authorization, itemId, type);
    }
}
