package com.socialnet.mapper;

import com.socialnet.dto.request.CommentRq;
import com.socialnet.dto.response.CommentRs;
import org.mapstruct.*;
import com.socialnet.entity.postrelated.PostComment;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                PersonMapper.class
        }
)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    CommentRs postCommentToCommentRs(PostComment postComment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PostComment commentRqToPostComment(CommentRq commentRq, @MappingTarget PostComment postComment);
}
