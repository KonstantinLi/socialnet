package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.FriendShip;
import ru.skillbox.socialnet.entity.enums.FriendShipStatus;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.PostComment;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.exception.InternalServerErrorException;
import ru.skillbox.socialnet.exception.NoRecordFoundException;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.*;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    private final JwtTokenUtils jwtTokenUtils;

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Transactional
    public CommonRs<CommentRs> createComment(String authorization, Long postId, CommentRq commentRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        if (commentRq.getCommentText() == null || commentRq.getCommentText().isBlank()) {
            throw new BadRequestException("Comment text is absent");
        }

        Post post = fetchPost(postId, false);

        PostComment postComment = new PostComment();

        postComment.setPost(post);
        postComment.setAuthor(post.getAuthor());
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
                        commentRq.isDeleted == null ? false : !commentRq.isDeleted
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

        List<PostComment> postComments;
        long total;

        try {
            postComments = postCommentsRepository.findAllByPostIdAndIsDeleted(
                    post.getId(),
                    false,
                    PageRequest.of(
                            offset, perPage,
                            Sort.by("time").descending()
                    )
            );
            total = postCommentsRepository.countByPostIdAndIsDeleted(
                    post.getId(), false
            );
        } catch (Exception e) {
            throw new InternalServerErrorException("getComments", e);
        }

        return getListPostCommentResponse(postComments, total, myId, offset, perPage);
    }


    protected PostComment fetchPostComment(Long id, Long postId, Boolean isDeleted) {
        Optional<PostComment> optionalPostComment;

        try {
            optionalPostComment = postCommentsRepository.findByIdAndPostIdAndIsDeleted(id, postId, isDeleted);
        } catch (Exception e) {
            throw new InternalServerErrorException("fetchPostComment", e);
        }

        if (optionalPostComment.isEmpty()) {
            throw new NoRecordFoundException("Post comment record " + id + " not found in the post of " + postId);
        }

        return optionalPostComment.get();
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

    private CommonRs<CommentRs> updatePostComment(PostComment postComment, CommentRq commentRq, Long myId) {
        savePostComment(postComment, commentRq);

        return getPostCommentResponse(postComment, myId);
    }

    private void savePostComment(PostComment postComment, CommentRq commentRq) {
        if (commentRq.getParentId() != null) {
            fetchPostComment(commentRq.getParentId(), postComment.getPost().getId(), false);
        }

        postComment.setParentId(null);

        try {
            postMapper.commentRqToPostComment(commentRq, postComment);
            postCommentsRepository.save(postComment);
        } catch (Exception e) {
            throw new InternalServerErrorException("savePostComment", e);
        }
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
        CommentRs commentRs;

        try {
            commentRs = postMapper.postCommentToCommentRs(postComment);

            commentRs.setLikes(likesRepository.countByTypeAndEntityId(LikeType.Comment, commentRs.getId()));
            commentRs.setMyLike(likesRepository.existsByPersonId(myId));
        } catch (Exception e) {
            throw new InternalServerErrorException("postCommentToCommentRs", e);
        }

        fillAuthor(commentRs.getAuthor(), myId);

        return commentRs;
    }

    private FriendShipStatus getFriendshipStatus(Long personId, Long destinationPersonId) {
        Optional<FriendShip> optionalFriendship;

        try {
            optionalFriendship = friendShipRepository
                    .findBySrcPersonIdAndDstPersonId(personId, destinationPersonId);
        } catch (Exception e) {
            throw new InternalServerErrorException("getFriendshipStatus", e);
        }

        if (optionalFriendship.isEmpty()) {
            return FriendShipStatus.UNKNOWN;
        }

        return optionalFriendship.get().getStatus();
    }

    private PersonRs fillAuthor(PersonRs personRs, Long myId) {
        FriendShipStatus friendshipStatus = getFriendshipStatus(personRs.getId(), myId);
        personRs.setFriendStatus(friendshipStatus.toString());
        personRs.setIsBlockedByCurrentUser(friendshipStatus == FriendShipStatus.BLOCKED);

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
