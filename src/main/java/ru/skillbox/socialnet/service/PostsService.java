package ru.skillbox.socialnet.service;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.dto.PostRq;

import java.util.List;

@Service
public class PostsService {
    public ResponseEntity<?> getPostById(String authorization, Long id) {
        // TODO: PostsService getPostById
        return null;
    }

    public ResponseEntity<?> updateById(String authorization, Long id, PostRq postRq) {
        // TODO: PostsService updateById
        return null;
    }

    public ResponseEntity<?> deleteById(String authorization, Long id) {
        // TODO: PostsService deleteById
        return null;
    }

    public ResponseEntity<?> recoverPostById(String authorization, Long id) {
        // TODO: PostsService recoverPostById
        return null;
    }

    public ResponseEntity<?> getWall(String authorization, Long id, Integer offset, Integer perPage) {
        // TODO: PostsService getWall
        return null;
    }

    public ResponseEntity<?> createPost(String authorization, Long publishDate, Long id, PostRq postRq) {
        // TODO: PostsService createPost
        return null;
    }

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
        // TODO: PostsService getPostsByQuery
        return null;
    }

    public ResponseEntity<?> getFeeds(String authorization, Integer offset, Integer perPage) {
        // TODO: PostsService getFeeds
        return null;
    }
}
