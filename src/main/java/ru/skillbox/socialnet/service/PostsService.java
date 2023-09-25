package ru.skillbox.socialnet.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.Friendship;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.FriendshipStatus;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.enums.PostType;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.Tag;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.exception.InternalServerErrorException;
import ru.skillbox.socialnet.exception.NoRecordFoundException;
import ru.skillbox.socialnet.mapper.LocalDateTimeConverter;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.*;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final PersonsRepository personsRepository;
    private final LikesRepository likesRepository;
    private final FriendshipsRepository friendshipsRepository;
    private final WeatherRepository weatherRepository;

    private final JwtTokenUtils jwtTokenUtils;
    private final EntityManager entityManager;

    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Transactional
    public CommonRsPostRs createPost(String authorization, Long publishDate, Long id, PostRq postRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        if (postRq.getTitle() == null || postRq.getTitle().isBlank()) {
            throw new BadRequestException("Post title is absent");
        }
        if (postRq.getPostText() == null || postRq.getPostText().isBlank()) {
            throw new BadRequestException("Post text is absent");
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
    public CommonRsPostRs updateById(String authorization, Long id, PostRq postRq) {
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
    public CommonRsPostRs deleteById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(true);

        return updateById(authorization, id, postRq);
    }

    @Transactional
    public CommonRsPostRs recoverPostById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(false);

        return updateById(authorization, id, postRq);
    }

    @Transactional
    public CommonRsPostRs getPostById(String authorization, Long id) {
        Long myId = jwtTokenUtils.getId(authorization);

        return getPostResponse(
                fetchPost(id, null),
                myId
        );
    }

    @Transactional
    public CommonRsListPostRs getPostsByQuery(
            String authorization,
            String author,
            Long dateFrom,
            Long dateTo,
            Integer offset,
            Integer perPage,
            List<String> tags,
            String text
    ) {
        Long myId = jwtTokenUtils.getId(authorization);

        Set<Long> personIds = null;

        try {
            if (author != null && !author.isBlank()) {
                String[] name = author.trim().split("\\s+", 2);

                switch (name.length) {
                    case 1 -> {
                        personIds = personsRepository.findAllByFirstNameAndIsDeleted(
                                    name[0], false
                                )
                                .stream()
                                .map(Person::getId)
                                .collect(Collectors.toSet());
                        personIds.addAll(personsRepository.findAllByLastNameAndIsDeleted(
                                    name[0], false
                                )
                                .stream()
                                .map(Person::getId)
                                .toList());
                    }
                    case 2 -> personIds = personsRepository.findAllByFirstNameAndLastNameAndIsDeleted(
                                name[0], name[1], false
                            )
                            .stream()
                            .map(Person::getId)
                            .collect(Collectors.toSet());
                }
            }

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            Boolean isDeleted = false;

            List<Predicate> predicatesPage = new ArrayList<>();
            CriteriaQuery<Post> pageQuery = builder.createQuery(Post.class);
            Root<Post> pageRoot = pageQuery.from(Post.class);
            pageQuery.select(pageRoot);

            List<Predicate> predicatesCount = new ArrayList<>();
            CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
            Root<Post> countRoot = countQuery.from(Post.class);
            countQuery.select(builder.count(countRoot));

            predicatesPage.add(builder.equal(pageRoot.get("isDeleted"), isDeleted));
            predicatesCount.add(builder.equal(countRoot.get("isDeleted"), isDeleted));
            if (personIds != null) {
                predicatesPage.add(pageRoot.get("authorId").in(personIds));
                predicatesCount.add(countRoot.get("authorId").in(personIds));
            }
            if (dateFrom != null) {
                Timestamp timestampFrom = new Timestamp(dateFrom);
                LocalDateTime timeFrom = localDateTimeConverter.convertToDatabaseColumn(timestampFrom);
                predicatesPage.add(builder.greaterThanOrEqualTo(pageRoot.get("time"), timeFrom));
                predicatesCount.add(builder.greaterThanOrEqualTo(countRoot.get("time"), timeFrom));
            }
            if (dateTo != null) {
                Timestamp timestampTo = new Timestamp(dateTo);
                predicatesPage.add(builder.lessThanOrEqualTo(pageRoot.get("time"), timestampTo));
                predicatesCount.add(builder.lessThanOrEqualTo(countRoot.get("time"), timestampTo));
            }

            // TODO: getPostsByQuery tags & text

            pageQuery.where(builder.and(predicatesPage.toArray(new Predicate[0])));
            countQuery.where(builder.and(predicatesCount.toArray(new Predicate[0])));
            pageQuery.orderBy(builder.desc(pageRoot.get("time")));

            return getListPostResponse(
                    entityManager
                            .createQuery(pageQuery)
                            .setMaxResults(perPage)
                            .setFirstResult(offset * perPage)
                            .getResultList(),
                    entityManager
                            .createQuery(countQuery)
                            .getSingleResult(),
                    myId, offset, perPage);

        } catch (Exception e) {
            throw new InternalServerErrorException("getPostsByQuery", e);
        }
    }

    @Transactional
    public CommonRsListPostRs getWall(String authorization, Long id, Integer offset, Integer perPage) {
        Long myId = jwtTokenUtils.getId(authorization);
        Person person = fetchPerson(id);

        List<Post> posts;
        long total;

        try {
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
        } catch (Exception e) {
            throw new InternalServerErrorException("getWall", e);
        }

        return getListPostResponse(posts, total, myId, offset, perPage);
    }

    @Transactional
    public CommonRsListPostRs getFeeds(String authorization, Integer offset, Integer perPage) {
        Long myId = jwtTokenUtils.getId(authorization);
        Person person = fetchPerson(myId);

        List<Post> posts;
        long total;

        try {
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
        } catch (Exception e) {
            throw new InternalServerErrorException("getFeeds", e);
        }

        return getListPostResponse(posts, total, myId, offset, perPage);
    }


    private Person fetchPerson(Long id) {
        Optional<Person> optionalPerson;

        try {
            optionalPerson = personsRepository.findById(id);
        } catch (Exception e) {
            throw new InternalServerErrorException("fetchPerson", e);
        }

        if (optionalPerson.isEmpty()) {
            throw new NoRecordFoundException("Person record " + id + " not found");
        }

        return optionalPerson.get();
    }

    private Post fetchPost(Long id, Boolean isDeleted) {
        Optional<Post> optionalPost;

        try {
            if (isDeleted == null) {
                optionalPost = postsRepository.findById(id);
            } else {
                optionalPost = postsRepository.findByIdAndIsDeleted(id, isDeleted);
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("fetchPost", e);
        }

        if (optionalPost.isEmpty()) {
            throw new NoRecordFoundException("Post record " + id + " not found");
        }

        return optionalPost.get();
    }

    private CommonRsPostRs updatePost(Post post, PostRq postRq, Long myId) {
        savePost(post, postRq);

        return getPostResponse(post, myId);
    }

    private void savePost(Post post, PostRq postRq) {
        try {
            post = postMapper.postRqToPost(postRq, post);
        } catch (Exception e) {
            throw new InternalServerErrorException("savePost", e);
        }

        fillTags(post);

        try {
            postsRepository.save(post);
        } catch (Exception e) {
            throw new InternalServerErrorException("savePost", e);
        }
    }

    private CommonRsListPostRs getListPostResponse(
            List<Post> posts, Long total, Long myId, Integer offset, Integer perPage
    ) {
        CommonRsListPostRs commonRsListPostRs = new CommonRsListPostRs();
        commonRsListPostRs.setOffset(offset);
        commonRsListPostRs.setItemPerPage(perPage);
        commonRsListPostRs.setPerPage(perPage);
        commonRsListPostRs.setTimestamp(new Date().getTime());
        commonRsListPostRs.setTotal(total);

        List<PostRs> postRsList = new ArrayList<>();

        for (Post post : posts) {
            postRsList.add(postToPostRs(post, myId));
        }

        commonRsListPostRs.setData(postRsList);

        return commonRsListPostRs;
    }

    private CommonRsPostRs getPostResponse(Post post, Long myId) {
        CommonRsPostRs commonRsPostRs = new CommonRsPostRs();

        commonRsPostRs.setData(postToPostRs(post, myId));

        return commonRsPostRs;
    }

    private PostRs postToPostRs(Post post, Long myId) {
        PostRs postRs;

        try {
            postRs = postMapper.postToPostRs(post);

            postRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Post, post.getId()));
            postRs.setMyLike(likesRepository.existsByPersonId(myId));
        } catch (Exception e) {
            throw new InternalServerErrorException("postToPostRs", e);
        }

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
        try {
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
        } catch (Exception e) {
            throw new InternalServerErrorException("fillTags", e);
        }

        return post;
    }

    private FriendshipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<Friendship> optionalFriendship;

        try {
            optionalFriendship = friendshipsRepository
                    .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);
        } catch (Exception e) {
            throw new InternalServerErrorException("getFriendshipStatus", e);
        }

        if (optionalFriendship.isEmpty()) {
            return FriendshipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }

    private PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendshipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendshipStatus.BLOCKED);

        Optional<Weather> optionalWeather;

        try {
            optionalWeather = weatherRepository.findByCity(personRs.getCity());

            if (optionalWeather.isPresent()) {
                personRs.setWeather(postMapper.weatherToWeatherRs(optionalWeather.get()));
            }
        } catch (Exception e) {
            throw new InternalServerErrorException("fillAuthor", e);
        }

        return personRs;
    }
}
