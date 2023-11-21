package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.locationrelated.Weather;
import ru.skillbox.socialnet.entity.personrelated.FriendShip;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.entity.postrelated.PostComment;
import ru.skillbox.socialnet.exception.PostCommentCreateException;
import ru.skillbox.socialnet.exception.PostCommentNotFoundException;
import ru.skillbox.socialnet.exception.PostNotFoundException;
import ru.skillbox.socialnet.mapper.CommentMapper;
import ru.skillbox.socialnet.mapper.WeatherMapper;
import ru.skillbox.socialnet.repository.*;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostsRepository postsRepository;
    private final LikesRepository likesRepository;
    private final FriendShipRepository friendShipRepository;
    private final WeatherRepository weatherRepository;
    private final PersonRepository personRepository;

    private final JwtTokenUtils jwtTokenUtils;
    private final CommentMapper commentMapper;
    private final WeatherMapper weatherMapper;

    @Transactional
    public CommonRs<CommentRs> createComment(String authorization, Long postId, CommentRq commentRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        if (commentRq.getCommentText() == null || commentRq.getCommentText().isBlank()) {
            throw new PostCommentCreateException("Текст комментария отсутствует");
        }

        PostComment postComment = new PostComment();

        postComment.setPostId(postId);
        postComment.setAuthor(personRepository.findById(myId).orElseThrow());
        postComment.setTime(LocalDateTime.now());
        postComment.setIsBlocked(false);
        postComment.setIsDeleted(false);

        return updatePostComment(postComment, commentRq, myId);
    }

    @Transactional
    public CommonRs<CommentRs> editComment(String authorization, Long id, Long commentId, CommentRq commentRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        return updatePostComment(
                fetchPostComment(
                        commentId, id,
                        commentRq.getIsDeleted() != null && !commentRq.getIsDeleted()
                ),
                commentRq, myId
        );
    }

    @Transactional
    public CommonRs<CommentRs> deleteComment(String authorization, Long id, Long commentId) {
        CommentRq commentRq = new CommentRq();
        commentRq.setIsDeleted(true);

        return editComment(authorization, id, commentId, commentRq);
    }

    @Transactional
    public CommonRs<CommentRs> recoverComment(String authorization, Long id, Long commentId) {
        CommentRq commentRq = new CommentRq();
        commentRq.setIsDeleted(false);

        return editComment(authorization, id, commentId, commentRq);
    }

    @Transactional
    public CommonRs<List<CommentRs>> getComments(String authorization, Long postId, Integer offset, Integer perPage) {
        Long myId = jwtTokenUtils.getId(authorization);
        Post post = fetchPost(postId, false);

        List<PostComment> postComments = postCommentsRepository.findAllByPostIdAndParentId(
                post.getId(),
                null,
                PageRequest.of(
                        offset, perPage,
                        Sort.by("time").descending()
                )
        );

        long total = postCommentsRepository.countByPostIdAndParentId(
                post.getId(), null
        );

        return getListPostCommentResponse(postComments, total, myId, offset, postComments.size());
    }


    protected PostComment fetchPostComment(Long id, Long postId, Boolean isDeleted) {
        Optional<PostComment> optionalPostComment = postCommentsRepository.findByIdAndPostIdAndIsDeleted(
                id, postId, isDeleted
        );

        if (optionalPostComment.isEmpty()) {
            throw new PostCommentNotFoundException(id);
        }

        return optionalPostComment.get();
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

    private CommonRs<CommentRs> updatePostComment(PostComment postComment, CommentRq commentRq, Long myId) {
        savePostComment(postComment, commentRq);

        return getPostCommentResponse(postComment, myId);
    }

    private void savePostComment(PostComment postComment, CommentRq commentRq) {
        if (commentRq.getParentId() != null) {
            PostComment parentPostComment = fetchPostComment(
                    commentRq.getParentId(), postComment.getPostId(), false
            );

            if (parentPostComment.getParentId() != null) {
                throw new PostCommentCreateException("Sub-comment of sub-comment is not allowed");
            }
        }

        commentMapper.commentRqToPostComment(commentRq, postComment);
        postCommentsRepository.save(postComment);
    }

    private CommonRs<List<CommentRs>> getListPostCommentResponse(
            List<PostComment> postComments, Long total, Long myId, Integer offset, Integer perPage
    ) {
        CommonRs<List<CommentRs>> commonRsListCommentRs = new CommonRs<>();
        commonRsListCommentRs.setOffset(offset);
        commonRsListCommentRs.setItemPerPage(perPage);
        commonRsListCommentRs.setPerPage(perPage);
        commonRsListCommentRs.setTotal(total);

        List<CommentRs> commentRsList = new ArrayList<>();

        for (PostComment postComment : postComments) {
            commentRsList.add(postCommentToCommentRs(postComment, myId));
        }

        commonRsListCommentRs.setData(commentRsList);

        return commonRsListCommentRs;
    }

    private CommonRs<CommentRs> getPostCommentResponse(PostComment postComment, Long myId) {
        CommonRs<CommentRs> commonRsCommentRs = new CommonRs<>();

        commonRsCommentRs.setData(postCommentToCommentRs(postComment, myId));

        return commonRsCommentRs;
    }

    private CommentRs postCommentToCommentRs(PostComment postComment, Long myId) {
        CommentRs commentRs = commentMapper.postCommentToCommentRs(postComment);

        commentRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Comment, commentRs.getId()));
        commentRs.setMyLike(likesRepository.existsByTypeAndEntityIdAndPersonId(LikeType.Comment, commentRs.getId(), myId));

        fillAuthor(commentRs.getAuthor(), myId);

        commentRs.getSubComments().forEach(subCommentRs -> {
            subCommentRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Comment, subCommentRs.getId()));
            subCommentRs.setMyLike(likesRepository.existsByTypeAndEntityIdAndPersonId(LikeType.Comment, subCommentRs.getId(), myId));

            fillAuthor(subCommentRs.getAuthor(), myId);
        });

        return commentRs;
    }

    private FriendShipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<FriendShip> optionalFriendShip = friendShipRepository
                .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);

        if (optionalFriendShip.isEmpty()) {
            return FriendShipStatus.UNKNOWN;
        }

        return optionalFriendShip.get().getStatus();
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
