package ru.skillbox.socialnet.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.MessageRs;
import ru.skillbox.socialnet.entity.dialogrelated.Message;
import ru.skillbox.socialnet.mapper.MessageMapper;
import ru.skillbox.socialnet.repository.MessageRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final JwtTokenUtils jwtTokenUtils;
  private final MessageRepository messageRepository;
  private final MessageMapper mapper;

  public MessageRs getMessageById(String authorization, Long messageId){
    Long userId = jwtTokenUtils.getId(authorization);
    MessageRs message = mapper.messageToMessageRs(messageRepository.getById(messageId));
    message.setIsSentByMe(message.getAuthorId().equals(userId));
    return message;
  }

  public CommonRs<List<MessageRs>> getMessagesByDialog(String authorization, Long dialogId, int offset, int perPage) {
    Long userId = jwtTokenUtils.getId(authorization);
    PageRequest pageable = PageRequest.of(offset, perPage);
    Page<Message> messages = messageRepository.getMessagesByDialog_Id(dialogId, pageable);
    List<MessageRs> data = messages.map(m -> {
                                    MessageRs messageRs = mapper.messageToMessageRs(m);
                                    messageRs.setIsSentByMe(messageRs.getAuthorId().equals(userId));
                                    return messageRs;
                                      }).toList();

    var result = new CommonRs<List<MessageRs>>();
    result.setData(data);
    result.setTotal(messages.getTotalElements());
    result.setOffset(offset);
    result.setPerPage(perPage);
    result.setItemPerPage(data.size());

    return result;
  }

  public MessageRs getLastMessageByDialogId(String authorization, Long dialogId){
    Long userId = jwtTokenUtils.getId(authorization);

    return messageRepository.getFirstByDialog_IdOrderByTimeDesc(dialogId).map(message -> {
      MessageRs messageRs = mapper.messageToMessageRs(message);
      messageRs.setIsSentByMe(messageRs.getAuthorId().equals(userId));
      return messageRs;
    }).orElse(null);
  }

  public CommonRs<ComplexRs> getCountUnreadedMessages(String authorization) {
    Long userId = jwtTokenUtils.getId(authorization);

    var result = new CommonRs<ComplexRs>();
    ComplexRs complexRs = new ComplexRs();
    complexRs.setId(userId);
    complexRs.setCount(messageRepository.countUnreadMessagesByUserId(userId));

    result.setData(complexRs);

    return result;
  }
}
