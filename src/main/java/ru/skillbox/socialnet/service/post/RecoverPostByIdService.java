package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecoverPostByIdService extends AbstractPostsService {
    public ResponseEntity<?> recoverPostById(String authorization, Long id) {
        // TODO: RecoverPostByIdService recoverPostById
        return null;
    }
}
