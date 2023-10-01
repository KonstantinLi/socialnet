package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.PostRs;
import ru.skillbox.socialnet.entity.postrelated.Post;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    default PostRs toRs(Post post) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        PostRs postRs = new PostRs();
        postRs.setAuthor(PersonMapper.INSTANCE.toRs(post.getAuthor()));
//        postRs.setComment();
        postRs.setId(post.getId());
        postRs.setIsBlocked(post.isBlocked());
//        postRs.setLikes(post.getLikes());
//        postRs.setMyLike();
        postRs.setPostText(post.getPostText());
        postRs.setTags(post.getTags().toString());
        postRs.setTime(post.getTime().format(formatter));
        postRs.setTitle(post.getTitle());
//        postRs.setType(post.getT);
        return postRs;
    }

    List<PostRs> toRsList (List<Post> postList);

}
