package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.socialnet.dto.response.MessageRs;
import ru.skillbox.socialnet.entity.dialogrelated.Message;

@Mapper(componentModel = "spring", uses = {PersonMapper.class})
public interface MessageMapper {

  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "recipientId", source = "recipient.id")
  MessageRs messageToMessageRs(Message message);

}
