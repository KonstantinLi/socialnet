package com.socialnet.exception;

import com.socialnet.entity.enums.LikeType;

public class LikeAlreadyExistsException extends BadRequestException {
    public LikeAlreadyExistsException(Long personId, LikeType likeType, Long itemId) {
        super("Like record by person " + personId + " type " + likeType
                + " item id " + itemId + " already exists");
    }
}