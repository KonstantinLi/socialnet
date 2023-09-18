package ru.skillbox.socialnet.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.PostComment;
import ru.skillbox.socialnet.mapper.PostMapper;
import ru.skillbox.socialnet.repository.FriendshipsRepository;
import ru.skillbox.socialnet.repository.PostCommentsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.repository.WeatherRepository;
import ru.skillbox.socialnet.util.ResponseEntityException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class PostCommentsService extends PostsAbstractService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostsRepository postsRepository;
    private final FriendshipsRepository friendshipsRepository;
    private final WeatherRepository weatherRepository;

    private final TransactionTemplate transactionTemplate;

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public ResponseEntity<?> createComment(String authorization, Long postId, CommentRq commentRq) {
        Long myId;

        try {
            myId = getMyId(authorization);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(401));
        }

        if (commentRq.getCommentText() == null || commentRq.getCommentText().isBlank()) {
            return new ResponseEntity<>(
                    new ErrorRs("Comment text is absent"),
                    HttpStatusCode.valueOf(400));
        }

        return transactionTemplate.execute(
                (TransactionCallback<ResponseEntity<?>>) status -> {
                    Post post;
                    PostComment parentComment = null;

                    try {
                        post = fetchPost(status, postId, false);

                        if (commentRq.getParentId() != null) {
                            parentComment = fetchPostComment(status, commentRq.getParentId(), false);
                        }
                    } catch (ResponseEntityException e) {
                        return e.getResponseEntity();
                    }

                    PostComment postComment = new PostComment();

                    postComment.setPost(post);
                    postComment.setAuthor(post.getAuthor());
                    postComment.setTime(LocalDateTime.now());
                    postComment.setIsBlocked(false);
                    postComment.setIsDeleted(false);

                    if (parentComment != null) {
                        postComment.setParentId(parentComment.getId());
                    }

                    return updatePostComment(status, postComment, commentRq, myId);
                }
        );
    }

    public ResponseEntity<?> editComment(String authorization, Long id, Long commentId, CommentRq commentRq) {
        // TODO: editComment
        return null;
    }

    public ResponseEntity<?> deleteComment(String authorization, Long id, Long commentId) {
        // TODO: deleteComment
        return null;
    }

    public ResponseEntity<?> recoverComment(String authorization, Long id, Long commentId) {
        // TODO: recoverComment
        return null;
    }

    public ResponseEntity<?> getComments(String authorization, Long postId, Integer offset, Integer perPage) {
        // TODO: getComments
        return null;
    }


    protected PostComment fetchPostComment(
            TransactionStatus status, Long id, Boolean isDeleted
    ) throws ResponseEntityException {
        Optional<PostComment> optionalPostComment;

        try {
            optionalPostComment = postCommentsRepository.findByIdAndIsDeleted(id, isDeleted);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("fetchPostComment: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }

        if (optionalPostComment.isEmpty()) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs(ERROR_NO_RECORD_FOUND, "Post comment record " + id + " not found"),
                    HttpStatusCode.valueOf(400)
            ));
        }

        return optionalPostComment.get();
    }

    private ResponseEntity<?> updatePostComment(
            TransactionStatus status, PostComment postComment, CommentRq commentRq, Long myId
    ) {
        try {
            savePostComment(status, postComment, commentRq);
        } catch (ResponseEntityException e) {
            return e.getResponseEntity();
        }

        return getPostCommentResponse(status, postComment, myId);
    }

    private void savePostComment(
            TransactionStatus status, PostComment postComment, CommentRq commentRq
    ) throws ResponseEntityException {
        try {
            postMapper.commentRqToPostComment(commentRq, postComment);
            postCommentsRepository.save(postComment);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("savePostComment: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }
    }

    private ResponseEntity<?> getPostCommentResponse(TransactionStatus status, PostComment postComment, Long myId) {
        CommonRsCommentRs commonRsCommentRs = new CommonRsCommentRs();

        try {
            commonRsCommentRs.setData(postCommentToCommentRs(status, postComment, myId));
        } catch (ResponseEntityException e) {
            return e.getResponseEntity();
        }

        return new ResponseEntity<>(
                commonRsCommentRs,
                HttpStatusCode.valueOf(200)
        );
    }

    private CommentRs postCommentToCommentRs(
            TransactionStatus status, PostComment postComment, Long myId
    ) throws ResponseEntityException {
        CommentRs commentRs = postMapper.postCommentToCommentRs(postComment);

        try {
            fillAuthor(commentRs.getAuthor(), myId);
        } catch (Exception e) {
            status.setRollbackOnly();
            throw new ResponseEntityException(new ResponseEntity<>(
                    new ErrorRs("postCommentToCommentRs: " + e.getMessage(), ExceptionUtils.getStackTrace(e)),
                    HttpStatusCode.valueOf(500)
            ));
        }

        return commentRs;
    }
}
