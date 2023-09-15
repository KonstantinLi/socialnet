package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.repository.PostsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeletePostByIdService extends AbstractPostsService {
    private final PostsRepository postsRepository;

    public ResponseEntity<?> deleteById(String authorization, Long id) {
        try {
            getMyId(authorization);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        Optional<Post> optionalPost;

        try {
            optionalPost = postsRepository.findById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        if (optionalPost.isEmpty()) {
            return new ResponseEntity<>(
                    new ErrorRs(ERROR_NO_RECORD_FOUND, "Post record " + id + " not found"),
                    HttpStatusCode.valueOf(400)
            );
        }

        try {
            postsRepository.delete(optionalPost.get());
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        return null;
    }
}
