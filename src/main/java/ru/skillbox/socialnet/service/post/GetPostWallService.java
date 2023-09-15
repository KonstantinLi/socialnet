package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPostWallService extends AbstractPostsService {
    public ResponseEntity<?> getWall(String authorization, Long id, Integer offset, Integer perPage) {
        // TODO: GetPostWallService getWall
        return null;
    }
}
