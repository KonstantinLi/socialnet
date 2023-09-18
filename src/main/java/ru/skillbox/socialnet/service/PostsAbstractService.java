package ru.skillbox.socialnet.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionStatus;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.Friendship;
import ru.skillbox.socialnet.entity.enums.FriendshipStatus;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.FriendshipsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;
import ru.skillbox.socialnet.util.ResponseEntityException;

import java.util.Optional;

public abstract class PostsAbstractService {
    public abstract PostsRepository getPostsRepository();
    public abstract FriendshipsRepository getFriendshipsRepository();
    public abstract WeatherRepository getWeatherRepository();
    public abstract PostMapper getPostMapper();

    public static final String ERROR_NO_RECORD_FOUND = "No record found";

    protected Post fetchPost(TransactionStatus status, Long id, Boolean isDeleted) throws ResponseEntityException {
        Optional<Post> optionalPost;

        try {
            optionalPost = getPostsRepository().findByIdAndIsDeleted(id, isDeleted);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("fetchPost: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }

        if (optionalPost.isEmpty()) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs(ERROR_NO_RECORD_FOUND, "Post record " + id + " not found"),
                    HttpStatusCode.valueOf(400)
            ));
        }

        return optionalPost.get();
    }

    protected FriendshipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<Friendship> optionalFriendship = getFriendshipsRepository()
                .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);

        if (optionalFriendship.isEmpty()) {
            return FriendshipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }

    protected PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendshipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendshipStatus.BLOCKED);

        Optional<Weather> optionalWeather = getWeatherRepository().findByCity(personRs.getCity());

        if (optionalWeather.isPresent()) {
            personRs.setWeather(getPostMapper().weatherToWeatherRs(optionalWeather.get()));
        }

        return personRs;
    }

    protected long getMyId(String authorization) throws AuthenticationException {
        // TODO: getMyId
        //throw new SessionAuthenticationException("Authorization check failed");
        return 123l;
    }
}
