package ru.skillbox.socialnet.service;

import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.Friendship;
import ru.skillbox.socialnet.entity.enums.FriendshipStatus;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.exception.InternalServerErrorException;
import ru.skillbox.socialnet.exception.NoRecordFoundException;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.FriendshipsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;

import java.util.Optional;

public abstract class PostsAbstractService {
    public abstract PostsRepository getPostsRepository();
    public abstract FriendshipsRepository getFriendshipsRepository();
    public abstract WeatherRepository getWeatherRepository();
    public abstract PostMapper getPostMapper();


    protected Post fetchPost(Long id, Boolean isDeleted) {
        Optional<Post> optionalPost;

        try {
            if (isDeleted == null) {
                optionalPost = getPostsRepository().findById(id);
            } else {
                optionalPost = getPostsRepository().findByIdAndIsDeleted(id, isDeleted);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("fetchPost", e);
        }

        if (optionalPost.isEmpty()) {
            throw new NoRecordFoundException("Post record " + id + " not found");
        }

        return optionalPost.get();
    }

    protected FriendshipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<Friendship> optionalFriendship;

        try {
            optionalFriendship = getFriendshipsRepository()
                .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);
        } catch (Exception e) {
            throw new InternalServerErrorException("getFriendshipStatus", e);
        }

        if (optionalFriendship.isEmpty()) {
            return FriendshipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }

    protected PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendshipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendshipStatus.BLOCKED);

        Optional<Weather> optionalWeather;

        try {
            optionalWeather = getWeatherRepository().findByCity(personRs.getCity());

            if (optionalWeather.isPresent()) {
                personRs.setWeather(getPostMapper().weatherToWeatherRs(optionalWeather.get()));
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("fillAuthor", e);
        }


        return personRs;
    }

    protected long getMyId(String authorization) {
        // TODO: getMyId
        //throw new UnauthorizedException();
        return 123l;
    }
}
