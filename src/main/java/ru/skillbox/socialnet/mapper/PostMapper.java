package ru.skillbox.socialnet.mapper;

import org.mapstruct.*;

import ru.skillbox.socialnet.dto.request.CommentRq;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.PostRs;

import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.PostComment;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                CommentMapper.class,
                TagMapper.class,
                PersonMapper.class
        }
)
public interface PostMapper {
    PostRs postToPostRs(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post postRqToPost(PostRq postRq, @MappingTarget Post post);
}
