package ru.skillbox.socialnet.mapper;

import org.mapstruct.*;
import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.entity.postrelated.PostComment;

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
