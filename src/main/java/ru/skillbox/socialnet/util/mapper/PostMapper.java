package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.Tag;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "time", target = "time", dateFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    PostRs toRs (Post post);

    default String toRs(Tag tag) {
        return tag.getTag();
    }

    List<PostRs> toRsList (List<Post> postList);

}
