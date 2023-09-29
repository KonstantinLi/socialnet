package ru.skillbox.socialnet.exception.like;

import ru.skillbox.socialnet.entity.enums.LikeType;
import ru.skillbox.socialnet.exception.BadRequestException;

public class LikeNotFoundException extends BadRequestException {
    public LikeNotFoundException(Long personId, LikeType likeType, Long itemId) {
        super("Like record by person " + personId + " type " + likeType
                + " item id " + itemId + " not found");
    }
}