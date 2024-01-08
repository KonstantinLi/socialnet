package com.socialnet.exception;

import com.socialnet.entity.enums.FriendShipStatus;

public class FriendShipNotFoundException extends BadRequestException {
    private final FriendShipStatus status;

    public FriendShipNotFoundException(FriendShipStatus status) {
        super("запись о дружбе не найдена");
        this.status = status;
    }

    @Override
    public String getMessage() {
        String msg;
        switch (status) {
            case BLOCKED -> msg = "запись о блокировке пользователя не найдена";
            case RECEIVED_REQUEST -> msg = "входящий запрос на дружбу не найден";
            case REQUEST -> msg = "исходящий запрос на дружбу не найден";
            default -> msg = "запись о дружбе не найдена";
        }
        return msg;
    }
}
