package ru.skillbox.socialnet.exception.like;

import ru.skillbox.socialnet.exception.BadRequestException;

public class LikeTargetNotProvidedException extends BadRequestException {
    public LikeTargetNotProvidedException() {
        super("Like type and target item id must be provided");
    }
}