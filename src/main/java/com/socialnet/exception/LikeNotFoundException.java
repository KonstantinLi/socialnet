package com.socialnet.exception;

import com.socialnet.entity.enums.LikeType;

public class LikeNotFoundException extends BadRequestException {
    public LikeNotFoundException(Long personId, LikeType likeType, Long itemId) {
        super("Like record by person " + personId + " type " + likeType
                + " item id " + itemId + " not found");
    }
}