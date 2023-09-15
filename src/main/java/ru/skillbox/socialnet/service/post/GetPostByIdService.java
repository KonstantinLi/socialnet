package ru.skillbox.socialnet.service.post;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.Friendship;
import ru.skillbox.socialnet.entity.enums.FriendshipStatus;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.enums.PostType;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.FriendshipsRepository;
import ru.skillbox.socialnet.repository.LikesRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPostByIdService extends AbstractPostsService {
    private final PostsRepository postsRepository;
    private final LikesRepository likesRepository;
    private final FriendshipsRepository friendshipsRepository;
    private final WeatherRepository weatherRepository;
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<?> getPostById(String authorization, Long id) {
        Long myId;

        try {
            myId = getMyId(authorization);
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

        // TODO: GetPostByIdService getPostById - test mapping
        PostRs postRs = postMapper.postToPostRs(optionalPost.get());

        try {
            postRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Post, id));
            postRs.setMyLike(likesRepository.existsByPersonId(myId));

            fillAuthor(postRs.getAuthor(), myId);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorRs(e.getMessage()), HttpStatusCode.valueOf(500)
            );
        }

        postRs.setType(String.valueOf(optionalPost.get().getIsDeleted() ? PostType.DELETED : PostType.POSTED));
        postRs.setComments(postRs.getComments().stream()
                .filter(commentRs -> commentRs.getParentId() == null).toList()
        );

        CommonRsPostRs commonRsPostRs = new CommonRsPostRs();
        commonRsPostRs.setData(postRs);
        // TODO: GetPostByIdService getPostById - other response fields filling

        return new ResponseEntity<>(
                commonRsPostRs,
                HttpStatusCode.valueOf(200)
        );
    }

    private PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendshipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendshipStatus.BLOCKED);

        Optional<Weather> optionalWeather = weatherRepository.findByCity(personRs.getCity());

        if (optionalWeather.isPresent()) {
            personRs.setWeather(postMapper.weatherToWeatherRs(optionalWeather.get()));
        }

        return personRs;
    }

    private FriendshipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<Friendship> optionalFriendship = friendshipsRepository
                .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);

        if (optionalFriendship.isEmpty()) {
            return FriendshipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }
}
