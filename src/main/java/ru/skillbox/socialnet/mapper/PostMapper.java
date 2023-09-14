package ru.skillbox.socialnet.mapper;

import org.mapstruct.*;
import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.Tag;

@Mapper
public interface PostMapper {
    PostRs postToPostRs(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post postRqToPost(PostRq postRq, @MappingTarget Post post);

    @Mapping(target = ".", source = "tag")
    String tagToString(Tag tag);

    @Mapping(target = "tag", source = ".")
    Tag StringToTag(String string);
}
