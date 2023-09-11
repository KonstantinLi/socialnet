package ru.skillbox.socialnet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.data.dto.LikeRq;
import ru.skillbox.socialnet.services.LikesService;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @GetMapping
    public ResponseEntity<?> getLikes(
            @RequestParam String authorization,
            @RequestParam("item_id") Integer itemId,
            @RequestParam String type
    ) {
        return likesService.getLikes(authorization, itemId, type);
    }

    @PutMapping
    public ResponseEntity<?> putLike(
            @RequestParam String authorization,
            @RequestBody LikeRq likeRq
    ) {
        return likesService.putLike(authorization, likeRq);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteLike(
            @RequestParam String authorization,
            @RequestParam("item_id") Integer itemId,
            @RequestParam String type
    ) {
        return likesService.deleteLike(authorization, itemId, type);
    }
}
