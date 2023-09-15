package ru.skillbox.socialnet.mapper;

import org.mapstruct.*;

import ru.skillbox.socialnet.dto.request.PostRq;
import ru.skillbox.socialnet.dto.response.CommentRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.dto.response.PostRs;

import ru.skillbox.socialnet.dto.response.WeatherRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.other.Weather;
import ru.skillbox.socialnet.entity.post.Post;
import ru.skillbox.socialnet.entity.post.PostComment;
import ru.skillbox.socialnet.entity.post.Tag;

@Mapper
public interface PostMapper {
    PostRs postToPostRs(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post postRqToPost(PostRq postRq, @MappingTarget Post post);

    default String tagToString(Tag tag) {
        return tag.getTag();
    }

    @Mapping(target = "tag", source = ".")
    Tag StringToTag(String string);

    @Mapping(target = "messagesPermission", source = "messagePermissions")
    @Mapping(target = "online", source = "onlineStatus")
    @Mapping(target = "userDeleted", source = "isDeleted")
    PersonRs personToPersonRs(Person person);

    CommentRs postCommentToCommentRs(PostComment postComment);

    WeatherRs weatherToWeatherRs(Weather weather);
}
