package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFeedsService extends AbstractPostsService {
    public ResponseEntity<?> getFeeds(String authorization, Integer offset, Integer perPage) {
        // TODO: GetFeedsService getFeeds
        return null;
    }
}
