package com.socialnet.mapper;

import com.socialnet.dto.request.PostRq;
import com.socialnet.dto.response.PostRs;
import org.mapstruct.*;
import com.socialnet.entity.postrelated.Post;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                CommentMapper.class,
                TagMapper.class,
                PersonMapper.class
        }
)
public interface PostMapper {

    @Mapping(source = "time", target = "time", dateFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    PostRs postToPostRs(Post post);

    List<PostRs> listPostToListPostRs(List<Post> posts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post postRqToPost(PostRq postRq, @MappingTarget Post post);
}
