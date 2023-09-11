package ru.skillbox.socialnet.services;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.data.dto.LikeRq;

@Service
public class LikesService {
    public ResponseEntity<?> getLikes(String authorization, Integer itemId, String type) {
        // TODO: LikesService getLikes
        return null;
    }

    public ResponseEntity<?> putLike(String authorization, LikeRq likeRq) {
        // TODO: LikesService putLike
        return null;
    }

    public ResponseEntity<?> deleteLike(String authorization, Integer itemId, String type) {
        // TODO: LikesService deleteLike
        return null;
    }
}
