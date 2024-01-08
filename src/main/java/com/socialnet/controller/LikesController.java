package com.socialnet.controller;

import com.socialnet.dto.request.LikeRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.LikeRs;
import com.socialnet.entity.enums.LikeType;
import com.socialnet.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @GetMapping
    public CommonRs<LikeRs> getLikes(
            @RequestHeader String authorization,
            @RequestParam("item_id") Long itemId,
            @RequestParam LikeType type
    ) {
        return likesService.getLikes(authorization, itemId, type);
    }

    @PutMapping
    public CommonRs<LikeRs> putLike(
            @RequestHeader String authorization,
            @RequestBody LikeRq likeRq
    ) {
        return likesService.putLike(authorization, likeRq);
    }

    @DeleteMapping
    public CommonRs<LikeRs> deleteLike(
            @RequestHeader String authorization,
            @RequestParam("item_id") Long itemId,
            @RequestParam LikeType type
    ) {
        return likesService.deleteLike(authorization, itemId, type);
    }
}
