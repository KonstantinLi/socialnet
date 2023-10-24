package ru.skillbox.socialnet.exception;

public class LikeTargetNotProvidedException extends BadRequestException {
    public LikeTargetNotProvidedException() {
        super("Like type and target item id must be provided");
    }
}