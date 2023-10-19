package ru.skillbox.socialnet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.MessageRs;
import ru.skillbox.socialnet.dto.websocket.MessageCommonWs;
import ru.skillbox.socialnet.dto.websocket.MessageWs;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;
import ru.skillbox.socialnet.entity.dialogrelated.Message;
import ru.skillbox.socialnet.entity.enums.ReadStatus;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.MessageException;
import ru.skillbox.socialnet.exception.person.PersonNotFoundException;
import ru.skillbox.socialnet.mapper.MessageMapper;
import ru.skillbox.socialnet.repository.DialogRepository;
import ru.skillbox.socialnet.repository.MessageRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final JwtTokenUtils jwtTokenUtils;
  private final MessageRepository messageRepository;
  private final PersonRepository personRepository; 
  private final DialogRepository dialogRepository;
  private final MessageMapper messageMapper;


  public CommonRs<List<MessageRs>> getMessagesByDialog(String authorization, Long dialogId, int offset, int perPage) {
    Long userId = jwtTokenUtils.getId(authorization);
    PageRequest pageable = PageRequest.of(offset, perPage);
    Page<Message> messages = messageRepository.getMessagesByDialogId(dialogId, pageable);
    List<MessageRs> data = messages.map(m -> {
                                    MessageRs messageRs = messageMapper.messageToMessageRs(m);
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

  public CommonRs<ComplexRs> getCountUnreadedMessages(String authorization) {
    Long userId = jwtTokenUtils.getId(authorization);

    var result = new CommonRs<ComplexRs>();
    ComplexRs complexRs = new ComplexRs();
    complexRs.setId(userId);
    complexRs.setCount(messageRepository.countUnreadMessagesByUserId(userId));
    result.setData(complexRs);
    return result;
  }


  @Transactional
  public MessageWs processMessage(Long dialogId, MessageWs messageWs) {
    Optional<Dialog> dialogOpt = dialogRepository.findById(dialogId);
    if (dialogOpt.isEmpty()) throw MessageException.dialogNotFound(dialogId);

    var authorId = messageWs.getAuthorId();
    Optional<Person> authorOpt = personRepository.findById(authorId);
    if (authorOpt.isEmpty()) throw new PersonNotFoundException(authorId);

    Message message = new Message();
    message.setMessageText(messageWs.getMessageText());
    message.setReadStatus(ReadStatus.UNREAD);
    message.setTime(LocalDateTime.now());

    Dialog dialog = dialogOpt.get();
    Person author = authorOpt.get();
    Person recipient = this.getRecipientByDialog(author, dialog);

    message.setDialog(dialog);
    message.setAuthor(author);
    message.setRecipient(recipient);

    var savedMessage = messageRepository.save(message);
    this.updateDialogLastMessage(dialogId, savedMessage);

    return messageMapper.messageToMessageWs(savedMessage, messageWs.getToken());
  }

  public Long editMessage(MessageCommonWs messageCommonWs) {
    var messageId = messageCommonWs.getMessageId();
    Optional<Message> messageOpt = messageRepository.findById(messageId);
    if (messageOpt.isEmpty()) throw MessageException.messageNotFound(messageId);

    var message = messageOpt.get();
    message.setMessageText(messageCommonWs.getMessageText());
    messageRepository.save(message);

    return message.getDialog().getId();
  }

  private Person getRecipientByDialog(Person author, Dialog dialog) {
    return Objects.equals(dialog.getFirstPerson().getId(), author.getId()) ? dialog.getSecondPerson() : dialog.getFirstPerson();
  }

  public void deleteMessages(MessageCommonWs messageCommonWs) {
    messageCommonWs.getMessageIds().stream()
        .map(messageRepository::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(message -> {
          message.setDeleted(true);
          messageRepository.save(message);
        });
  }

  public void recoverMessage(MessageCommonWs messageCommonWs) {
      messageRepository.findById(messageCommonWs.getMessageId()).ifPresent(message -> {
          message.setDeleted(false);
          messageRepository.save(message);
      });
  }

  private void updateDialogLastMessage(Long dialogId, Message message){
    dialogRepository.findById(dialogId).ifPresent(dialog -> {
      dialog.setLastMessageId(message.getId());
      dialog.setLastActiveTime(message.getTime());
      dialogRepository.save(dialog);
    });

  }

}
