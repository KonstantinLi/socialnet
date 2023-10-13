package ru.skillbox.socialnet.service;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.dto.parameters.GetPostsSearchPs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.enums.PostType;
import ru.skillbox.socialnet.entity.locationrelated.Weather;
import ru.skillbox.socialnet.entity.personrelated.FriendShip;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.entity.postrelated.Tag;
import ru.skillbox.socialnet.exception.person.PersonNotFoundException;
import ru.skillbox.socialnet.exception.post.PostCreateException;
import ru.skillbox.socialnet.exception.post.PostNotFoundException;
import ru.skillbox.socialnet.mapper.LocalDateTimeConverter;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.mapper.WeatherMapper;
import ru.skillbox.socialnet.repository.*;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class PostsService {

    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final PersonRepository personRepository;
    private final LikesRepository likesRepository;
    private final FriendShipRepository friendShipRepository;
    private final WeatherRepository weatherRepository;

    private final JwtTokenUtils jwtTokenUtils;
    private final PostMapper postMapper;
    private final WeatherMapper weatherMapper;
    private final EntityManager entityManager;

    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

    @Transactional
    public CommonRs<PostRs> createPost(String authorization, Long publishDate, Long id, PostRq postRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        if (postRq.getTitle() == null || postRq.getTitle().isBlank()) {
            throw new PostCreateException("Post title is absent");
        }
        if (postRq.getPostText() == null || postRq.getPostText().isBlank()) {
            throw new PostCreateException("Post text is absent");
        }

        Post post = new Post();

        post.setAuthor(fetchPerson(id));
        post.setTime(
                publishDate == null
                        ? LocalDateTime.now()
                        : localDateTimeConverter.convertToDatabaseColumn(new Timestamp(publishDate))
        );
        post.setIsBlocked(false);
        post.setIsDeleted(false);

        return updatePost(post, postRq, myId);
    }

    @Transactional
    public CommonRs<PostRs> updateById(String authorization, Long id, PostRq postRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        return updatePost(
                fetchPost(
                        id,
                        postRq.isDeleted == null ? false : !postRq.isDeleted
                ),
                postRq, myId
        );
    }

    @Transactional
    public CommonRs<PostRs> deleteById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(true);

        return updateById(authorization, id, postRq);
    }

    @Transactional
    public CommonRs<PostRs> recoverPostById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(false);

        return updateById(authorization, id, postRq);
    }

    @Transactional
    public CommonRs<PostRs> getPostById(String authorization, Long id) {
        Long myId = jwtTokenUtils.getId(authorization);

        return getPostResponse(
                fetchPost(id, null),
                myId
        );
    }

    @Transactional
    public CommonRs<List<PostRs>> getPostsByQuery(Long currentUserId,
                                                  GetPostsSearchPs getPostsSearchPs,
                                                  int offset,
                                                  int perPage) {
        Pageable nextPage = PageRequest.of(offset, perPage);
        CommonRs<List<PostRs>> result = new CommonRs<>();
        Page<Post> postPage = postsRepository.findPostsByQuery(getPostsSearchPs.getAuthor(),
                getPostsSearchPs.getDateFrom() / 1000,
                getPostsSearchPs.getDateTo() / 1000,
                getPostsSearchPs.getTags() == null,
                getPostsSearchPs.getTags(),
                getPostsSearchPs.getText(),
                nextPage);
        result.setData(postMapper.listPostToListPostRs(postPage.getContent()));
        result.setTotal(postPage.getTotalElements());
        result.setItemPerPage(postPage.getContent().size());
        result.setPerPage(perPage);
        result.setOffset(offset);

        return result;
    }


    @Transactional
    public CommonRs<List<PostRs>> getWall(String authorization, Long id, Integer offset, Integer perPage) {
        Long myId = jwtTokenUtils.getId(authorization);
        Person person = fetchPerson(id);

        List<Post> posts;
        long total;

        posts = postsRepository.findAllByAuthorIdAndIsDeleted(
                person.getId(),
                false,
                PageRequest.of(
                        offset, perPage,
                        Sort.by("time").descending()
                )
        );
        total = postsRepository.countByAuthorIdAndIsDeleted(
                person.getId(),
                false
        );

        return getListPostResponse(posts, total, myId, offset, perPage);
    }

    @Transactional
    public CommonRs<List<PostRs>> getFeeds(String authorization, Integer offset, Integer perPage) {
        Long myId = jwtTokenUtils.getId(authorization);
        Person person = fetchPerson(myId);

        List<Post> posts;
        long total;

        posts = postsRepository.findAllByIsDeletedAndTimeGreaterThan(
                false,
                person.getLastOnlineTime(),
                PageRequest.of(
                        offset, perPage,
                        Sort.by("time").descending()
                )
        );
        total = postsRepository.countByIsDeletedAndTimeGreaterThan(
                false,
                person.getLastOnlineTime()
        );

        return getListPostResponse(posts, total, myId, offset, perPage);
    }


    private Person fetchPerson(Long id) {
        Optional<Person> optionalPerson = personRepository.findById(id);

        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException(id);
        }

        return optionalPerson.get();
    }

    private Post fetchPost(Long id, Boolean isDeleted) {
        Optional<Post> optionalPost;

        if (isDeleted == null) {
            optionalPost = postsRepository.findById(id);
        } else {
            optionalPost = postsRepository.findByIdAndIsDeleted(id, isDeleted);
        }

        if (optionalPost.isEmpty()) {
            throw new PostNotFoundException(id);
        }

        return optionalPost.get();
    }

    private CommonRs<PostRs> updatePost(Post post, PostRq postRq, Long myId) {
        savePost(post, postRq);

        return getPostResponse(post, myId);
    }

    private void savePost(Post post, PostRq postRq) {
        fillTags(postMapper.postRqToPost(postRq, post));

        postsRepository.save(post);
    }

    private CommonRs<List<PostRs>> getListPostResponse(
            List<Post> posts, Long total, Long myId, Integer offset, Integer perPage
    ) {
        CommonRs<List<PostRs>> commonRsListPostRs = new CommonRs<>();
        commonRsListPostRs.setOffset(offset);
        commonRsListPostRs.setItemPerPage(perPage);
        commonRsListPostRs.setPerPage(perPage);
        commonRsListPostRs.setTotal(total);

        List<PostRs> postRsList = new ArrayList<>();

        for (Post post : posts) {
            postRsList.add(postToPostRs(post, myId));
        }

        commonRsListPostRs.setData(postRsList);

        return commonRsListPostRs;
    }

    private CommonRs<PostRs> getPostResponse(Post post, Long myId) {
        CommonRs<PostRs> commonRsPostRs = new CommonRs<>();

        commonRsPostRs.setData(postToPostRs(post, myId));

        return commonRsPostRs;
    }

    private PostRs postToPostRs(Post post, Long myId) {
        PostRs postRs = postMapper.postToPostRs(post);

        postRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.POST, post.getId()));
        postRs.setMyLike(likesRepository.existsByPersonId(myId));

        fillAuthor(postRs.getAuthor(), myId);

        postRs.setType(String.valueOf(
                post.getIsDeleted()
                        ? PostType.DELETED
                        : post.getTime().isAfter(LocalDateTime.now())
                                ? PostType.QUEUED
                                : PostType.POSTED
                )
        );
        postRs.setComments(postRs.getComments().stream()
                .filter(commentRs -> commentRs.getParentId() == null).collect(Collectors.toSet())
        );

        return postRs;
    }

    /**
     * Fills tags list of the post entity with correct tag entities, found by tag string.
     *
     * @param post Target post entity
     * @return The post entity given as parameter
     */
    private Post fillTags(Post post) {
        post.setTags(
                post.getTags().stream().map(tag -> {
                    Optional<Tag> optionalTag = tagsRepository.findByTag(tag.getTag());

                    if (optionalTag.isPresent()) {
                        return optionalTag.get();
                    }

                    tag.setId(null);
                    return tagsRepository.save(tag);
                }).collect(Collectors.toSet())
        );

        return post;
    }

    private FriendShipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<FriendShip> optionalFriendship = friendShipRepository
                    .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);

        if (optionalFriendship.isEmpty()) {
            return FriendShipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }

    private PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendShipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendShipStatus.BLOCKED);

        Optional<Weather> optionalWeather = weatherRepository.findByCity(personRs.getCity());

        if (optionalWeather.isPresent()) {
            personRs.setWeather(weatherMapper.weatherToWeatherRs(optionalWeather.get()));
        }

        return personRs;
    }
}
