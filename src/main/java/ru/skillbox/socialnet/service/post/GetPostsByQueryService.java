package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPostsByQueryService extends AbstractPostsService {
    public ResponseEntity<?> getPostsByQuery(
            String authorization,
            String author,
            Long dateFrom,
            Long dateTo,
            Integer offset,
            Integer perPage,
            List<String> tags,
            String text
    ) {
        // TODO: GetPostsByQueryService getPostsByQuery
        return null;
    }
}
