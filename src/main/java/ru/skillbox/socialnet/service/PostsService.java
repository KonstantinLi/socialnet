package ru.skillbox.socialnet.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.enums.PostType;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.Tag;
import ru.skillbox.socialnet.mapper.LocalDateTimeConverter;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.*;
import ru.skillbox.socialnet.util.ResponseEntityException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class PostsService extends PostsAbstractService {
    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final PersonsRepository personsRepository;
    private final LikesRepository likesRepository;
    private final FriendshipsRepository friendshipsRepository;
    private final WeatherRepository weatherRepository;

    private final TransactionTemplate transactionTemplate;
    private final EntityManager entityManager;

    private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public ResponseEntity<?> createPost(String authorization, Long publishDate, Long id, PostRq postRq) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        if (postRq.getTitle() == null || postRq.getTitle().isBlank()) {
            return new ResponseEntity<>(
                    new ErrorRs("Post title is absent"),
                    HttpStatusCode.valueOf(400));
        }

        if (postRq.getPostText() == null || postRq.getPostText().isBlank()) {
            return new ResponseEntity<>(
                    new ErrorRs("Post text is absent"),
                    HttpStatusCode.valueOf(400));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Person person;

                    try {
                        person = fetchPerson(status, id);
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    LocalDateTime postTime;

                    if (publishDate == null) {
                        postTime = LocalDateTime.now();
                    } else {
                        postTime = localDateTimeConverter.convertToDatabaseColumn(new Timestamp(publishDate));
                    }

                    Post post = new Post();

                    post.setAuthor(person);
                    post.setTime(postTime);
                    post.setIsBlocked(false);
                    post.setIsDeleted(false);

                    return updatePost(status, post, postRq, myId);
                }
        );
    }

    public ResponseEntity<?> updateById(String authorization, Long id, PostRq postRq) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Post post;

                    try {
                        post = fetchPost(status, id, postRq.isDeleted == null ? false : !postRq.isDeleted);
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    return updatePost(status, post, postRq, myId);
                });
    }

    public ResponseEntity<?> deleteById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(true);

        return updateById(authorization, id, postRq);
    }

    public ResponseEntity<?> recoverPostById(String authorization, Long id) {
        PostRq postRq = new PostRq();
        postRq.setIsDeleted(false);

        return updateById(authorization, id, postRq);
    }

    public ResponseEntity<?> getPostById(String authorization, Long id) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Post post;

                    try {
                        post = fetchPost(status, id, false);
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    return getPostResponse(status, post, myId);
                });
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
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Set<Long> personIds = null;

                    try {
                        if (author != null && !author.isBlank()) {
                            String[] name = author.trim().split("\\s+", 2);

                            switch (name.length) {
                                case 1 -> {
                                    personIds = personsRepository.findAllByFirstName(name[0])
                                            .stream()
                                            .map(Person::getId)
                                            .collect(Collectors.toSet());
                                    personIds.addAll(personsRepository.findAllByLastName(name[0])
                                            .stream()
                                            .map(Person::getId)
                                            .toList());
                                }
                                case 2 -> personIds = personsRepository.findAllByFirstNameAndLastName(name[0], name[1])
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

                        List<Post> posts = entityManager
                                .createQuery(pageQuery)
                                .setMaxResults(perPage)
                                .setFirstResult(offset * perPage)
                                .getResultList();

                        Long total = entityManager.createQuery(countQuery).getSingleResult();

                        return getListPostResponse(status,
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
                        status.setRollbackOnly();
                        return new ResponseEntity<>(
                                new ErrorRs("getPostsByQuery: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                                HttpStatusCode.valueOf(500)
                        );
                    }
                }
        );
    }

    public ResponseEntity<?> getWall(String authorization, Long id, Integer offset, Integer perPage) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Person person;

                    try {
                        person = fetchPerson(status, id);
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    try {
                        return getListPostResponse(status,
                                postsRepository.findAllByAuthorIdAndIsDeleted(
                                        person.getId(),
                                        false,
                                        PageRequest.of(
                                                offset, perPage,
                                                Sort.by("time").descending()
                                        )
                                ),
                                postsRepository.countByAuthorIdAndIsDeleted(
                                        person.getId(),
                                        false
                                ),
                                myId, offset, perPage
                        );
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        return new ResponseEntity<>(
                                new ErrorRs("getWall: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                                HttpStatusCode.valueOf(500)
                        );
                    }
                }
        );
    }

    public ResponseEntity<?> getFeeds(String authorization, Integer offset, Integer perPage) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Person person;

                    try {
                        person = fetchPerson(status, myId);
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    try {
                        return getListPostResponse(status,
                                postsRepository.findAllByIsDeletedAndTimeGreaterThan(
                                        false,
                                        person.getLastOnlineTime(),
                                        PageRequest.of(
                                                offset, perPage,
                                                Sort.by("time").descending()
                                        )
                                ),
                                postsRepository.countByIsDeletedAndTimeGreaterThan(
                                        false,
                                        person.getLastOnlineTime()
                                ),
                                myId, offset, perPage
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        status.setRollbackOnly();
                        return new ResponseEntity<>(
                                new ErrorRs("getFeeds: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                                HttpStatusCode.valueOf(500)
                        );
                    }
                }
        );
    }


    private Person fetchPerson(TransactionStatus status, Long id) throws ResponseEntityException {
        Optional<Person> optionalPerson;

        try {
            optionalPerson = personsRepository.findById(id);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("fetchPerson: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }

        if (optionalPerson.isEmpty()) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs(ERROR_NO_RECORD_FOUND, "Person record " + id + " not found"),
                    HttpStatusCode.valueOf(400)
            ));
        }

        return optionalPerson.get();
    }

    private ResponseEntity<?> updatePost(TransactionStatus status, Post post, PostRq postRq, Long myId) {
        try {
            savePost(status, post, postRq);
        } catch (ResponseEntityException e) {
            return e.getResponseEntity();
        }

        return getPostResponse(status, post, myId);
    }

    private void savePost(TransactionStatus status, Post post, PostRq postRq) throws ResponseEntityException {
        try {
            fillTags(postMapper.postRqToPost(postRq, post));
            postsRepository.save(post);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("savePost: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }
    }

    private ResponseEntity<?> getListPostResponse(
            TransactionStatus status, List<Post> posts, Long total, Long myId, Integer offset, Integer perPage
    ) {
        CommonRsListPostRs commonRsListPostRs = new CommonRsListPostRs();
        commonRsListPostRs.setOffset(offset);
        commonRsListPostRs.setItemPerPage(perPage);
        commonRsListPostRs.setPerPage(perPage);
        commonRsListPostRs.setTimestamp(new Date().getTime());
        commonRsListPostRs.setTotal(total);

        List<PostRs> postRsList = new ArrayList<>();

        try {
            for (Post post : posts) {
                postRsList.add(postToPostRs(status, post, myId));
            }
        } catch (ResponseEntityException e) {
            return e.getResponseEntity();
        }

        commonRsListPostRs.setData(postRsList);

        return new ResponseEntity<>(
                commonRsListPostRs,
                HttpStatusCode.valueOf(200)
        );
    }

    private ResponseEntity<?> getPostResponse(TransactionStatus status, Post post, Long myId) {
        CommonRsPostRs commonRsPostRs = new CommonRsPostRs();

        try {
            commonRsPostRs.setData(postToPostRs(status, post, myId));
        } catch (ResponseEntityException e) {
            return e.getResponseEntity();
        }

        return new ResponseEntity<>(
                commonRsPostRs,
                HttpStatusCode.valueOf(200)
        );
    }

    private PostRs postToPostRs(
            TransactionStatus status, Post post, Long myId
    ) throws ResponseEntityException {
        PostRs postRs = postMapper.postToPostRs(post);

        try {
            postRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Post, post.getId()));
            postRs.setMyLike(likesRepository.existsByPersonId(myId));

            fillAuthor(postRs.getAuthor(), myId);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("postToPostRs: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }

        postRs.setType(String.valueOf(post.getIsDeleted() ? PostType.DELETED : PostType.POSTED));
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
}
