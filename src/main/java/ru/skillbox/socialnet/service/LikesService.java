package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.entity.postrelated.Like;
import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.entity.postrelated.Post;
import ru.skillbox.socialnet.entity.postrelated.PostComment;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.exception.InternalServerErrorException;
import ru.skillbox.socialnet.exception.NoRecordFoundException;
import ru.skillbox.socialnet.repository.LikesRepository;
import ru.skillbox.socialnet.dto.request.LikeRq;
import ru.skillbox.socialnet.repository.PostCommentsRepository;
import ru.skillbox.socialnet.repository.PostsRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostsRepository postsRepository;
    private final PostCommentsRepository postCommentsRepository;

    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public CommonRs<LikeRs> getLikes(String authorization, Long itemId, LikeType type) {
        Long myId = jwtTokenUtils.getId(authorization);

        Like like = new Like();
        like.setType(type);
        like.setEntityId(itemId);

        return getLikeResponse(like);
    }

    @Transactional
    public CommonRs<LikeRs> putLike(String authorization, LikeRq likeRq) {
        Long myId = jwtTokenUtils.getId(authorization);

        if (likeRq.getType() == null || likeRq.getItemId() == null) {
            throw new BadRequestException("No like type or item id provided");
        }

        Optional<Like> optionalLike;

        try {
            optionalLike = likesRepository.findByPersonIdAndTypeAndEntityId(
                        myId, likeRq.getType(), likeRq.getItemId()
                    );
        } catch (Exception e) {
            throw new InternalServerErrorException("putLike", e);
        }

        if (optionalLike.isPresent()) {
            throw new BadRequestException(
                    "Like record by person " + myId + " of " + likeRq.getType()
                            + " id " + likeRq.getItemId() + " already exists"
            );
        }

        switch (likeRq.getType()) {
            case Post -> {
                Optional<Post> optionalPost;

                try {
                    optionalPost = postsRepository.findById(likeRq.getItemId());
                } catch (Exception e) {
                    throw new InternalServerErrorException("putLike", e);
                }

                if (optionalPost.isEmpty()) {
                    throw new NoRecordFoundException(
                            "Post record " + likeRq.getItemId() + " not found"
                    );
                }
            }
            case Comment -> {
                Optional<PostComment> optionalPostComment;

                try {
                    optionalPostComment = postCommentsRepository.findById(likeRq.getItemId());
                } catch (Exception e) {
                    throw new InternalServerErrorException("putLike", e);
                }

                if (optionalPostComment.isEmpty()) {
                    throw new NoRecordFoundException(
                            "Post comment record " + likeRq.getItemId() + " not found"
                    );
                }
            }
        }

        Like like = new Like();
        like.setPersonId(myId);
        like.setType(likeRq.getType());
        like.setEntityId(likeRq.getItemId());

        try {
            likesRepository.save(like);
        } catch (Exception e) {
            throw new InternalServerErrorException("putLike", e);
        }

        return getLikeResponse(like);
    }

    @Transactional
    public CommonRs<LikeRs> deleteLike(String authorization, Long itemId, LikeType type) {
        Long myId = jwtTokenUtils.getId(authorization);

        Optional<Like> optionalLike;

        try {
            optionalLike = likesRepository.findByPersonIdAndTypeAndEntityId(
                    myId, type, itemId
            );
        } catch (Exception e) {
            throw new InternalServerErrorException("deleteLike", e);
        }

        if (optionalLike.isEmpty()) {
            throw new NoRecordFoundException(
                    "Like record by person " + myId + " of " + type + " id " + itemId + " not found"
            );
        }

        try {
            likesRepository.deleteByPersonIdAndTypeAndEntityId(
                    myId, type, itemId
            );
        } catch (Exception e) {
            throw new InternalServerErrorException("deleteLike", e);
        }

        return getLikeResponse(optionalLike.get());
    }


    private CommonRs<LikeRs> getLikeResponse(Like like) {
        CommonRs<LikeRs> commonRsLikeRs = new CommonRs<>();
        LikeRs likeRs = new LikeRs();

        try {
            likeRs.setUsers(likesRepository.findAllByTypeAndEntityId(like.getType(), like.getEntityId())
                    .stream()
                    .map(Like::getPersonId)
                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            throw new InternalServerErrorException("getLikeResponse", e);
        }

        likeRs.setLikes(likeRs.getUsers().size());
        commonRsLikeRs.setData(likeRs);

        return commonRsLikeRs;
    }
}
