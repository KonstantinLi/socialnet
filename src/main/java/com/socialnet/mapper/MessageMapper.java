package com.socialnet.mapper;

import com.socialnet.dto.response.MessageRs;
import com.socialnet.dto.websocket.MessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.socialnet.entity.dialogrelated.Message;

import java.time.ZoneId;

@Mapper(componentModel = "spring", uses = {PersonMapper.class}, imports = ZoneId.class)
public interface MessageMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "recipientId", source = "recipient.id")
    MessageRs messageToMessageRs(Message message);

    @Mapping(target = "authorId", source = "message.author.id")
    @Mapping(target = "recipientId", source = "message.recipient.id")
    @Mapping(target = "time", expression = "java( message.getTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() )")
    MessageWs messageToMessageWs(Message message, String token);

}
