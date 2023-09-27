package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.skillbox.socialnet.entity.post.Tag;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface TagMapper {
    default String tagToString(Tag tag) {
        return tag.getTag();
    }

    default Tag StringToTag(String string) {
        Tag tag = new Tag();
        tag.setTag(string);
        return tag;
    }
}
