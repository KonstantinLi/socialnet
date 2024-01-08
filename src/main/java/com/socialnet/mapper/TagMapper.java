package com.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import com.socialnet.entity.postrelated.Tag;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface TagMapper {
    default String tagToString(Tag tag) {
        return tag.getTagName();
    }

    default Tag stringToTag(String string) {
        Tag tag = new Tag();
        tag.setTagName(string);
        return tag;
    }

}
