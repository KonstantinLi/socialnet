package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.entity.Like;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.repository.LikesRepository;
import ru.skillbox.socialnet.dto.request.LikeRq;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;

    public ResponseEntity<?> getLikes(String authorization, Long itemId, LikeType type) {
        try {
            long myId = getMyId(authorization);
            Optional<Like> like = likesRepository.findByPersonIdAndTypeAndEntityId(
                    myId, type, itemId
            );

            if (like.isEmpty()) {
                return new ResponseEntity<>(
                        new ErrorRs("No record to delete"),
                        HttpStatusCode.valueOf(400)
                );
            }

            likesRepository.delete(like.get());
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }
        return null;
    }

    public ResponseEntity<?> putLike(String authorization, LikeRq likeRq) {
        if (likeRq.getType() == null) {
            return new ResponseEntity<>(
                    new ErrorRs("Like type missed"),
                    HttpStatusCode.valueOf(400)
            );
        }

        if (likeRq.getItemId() == null) {
            switch (likeRq.getType()) {
                case Post -> {
                    return new ResponseEntity<>(
                            new ErrorRs("Post Id missed"),
                            HttpStatusCode.valueOf(400)
                    );
                }
                case Comment -> {
                    return new ResponseEntity<>(
                            new ErrorRs("Comment Id missed"),
                            HttpStatusCode.valueOf(400)
                    );
                }
            }
        }

        try {
            long myId = getMyId(authorization);
            Optional<Like> like = likesRepository.findByPersonIdAndTypeAndEntityId(
                    myId, likeRq.getType(), likeRq.getItemId()
            );

            if (like.isPresent()) {
                return new ResponseEntity<>(
                        new ErrorRs("Like already exists"),
                        HttpStatusCode.valueOf(400)
                );
            }

            Like newLike = new Like();
            newLike.setEntityId(likeRq.getItemId());
            newLike.setType(likeRq.getType());
            newLike.setPersonId(myId);

            likesRepository.save(newLike);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }
        return null;
    }

    public ResponseEntity<?> deleteLike(String authorization, Long itemId, LikeType type) {
        try {
            long myId = getMyId(authorization);
            Optional<Like> like = likesRepository.findByPersonIdAndTypeAndEntityId(
                    myId, type, itemId
            );

            if (like.isEmpty()) {
                return new ResponseEntity<>(
                        new ErrorRs("No record to delete"),
                        HttpStatusCode.valueOf(400)
                );
            }

            likesRepository.delete(like.get());
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }
        return null;
    }

    private long getMyId(String authorization) throws AuthenticationException {
        // TODO: LikesService getMyId
        return 123l;
    }
}
