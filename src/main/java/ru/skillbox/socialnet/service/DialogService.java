package ru.skillbox.socialnet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnet.dto.request.DialogUserShortListRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.DialogRs;
import ru.skillbox.socialnet.dto.response.MessageRs;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;
import ru.skillbox.socialnet.entity.dialogrelated.Message;
import ru.skillbox.socialnet.entity.enums.ReadStatus;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.exception.MessageException;
import ru.skillbox.socialnet.mapper.DialogMapper;
import ru.skillbox.socialnet.mapper.MessageMapper;
import ru.skillbox.socialnet.repository.DialogRepository;
import ru.skillbox.socialnet.repository.MessageRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class DialogService {

  private final JwtTokenUtils jwtTokenUtils;
  private final DialogRepository dialogRepository;
  private final MessageRepository messageRepository;


  @Transactional
  public CommonRs<List<DialogRs>> getDialogs(String authorization) {
    Long userId = jwtTokenUtils.getId(authorization);
    List<DialogRs> dialogsByUserId =
        dialogRepository.getDialogsByUserId(userId).stream()
            .map(dialog -> getDialogRs(authorization, dialog))
            .toList();

    var result = new CommonRs<List<DialogRs>>();
    result.setData(dialogsByUserId);
    result.setTotal((long) dialogsByUserId.size());
    return result;
  }

  private DialogRs getDialogRs(String authorization, Dialog dialog) {
    var dialogRs = DialogMapper.INSTANCE.dialogToDialogRs(dialog);

    if (dialog.getLastMessageId() != null) {
      var lastMessage = this.getMessageById(authorization, dialog.getLastMessageId());
      dialogRs.setLastMessage(lastMessage);
      dialogRs.setReadStatus(lastMessage.getReadStatus());
    }
    var countUnread = messageRepository.countUnreadMessagesByDialogId(dialog.getId());
    dialogRs.setUnreadCount(countUnread);

    return dialogRs;
  }


  @Transactional
  public CommonRs<ComplexRs> startDialog(String authorization, DialogUserShortListRq dialogListRq) {
    Long userId = jwtTokenUtils.getId(authorization);

    //создаем диалоги по списку пользователей
    dialogListRq.getUserIds().forEach(personId -> findOrCreateDialog(userId, personId));

    var result = new CommonRs<ComplexRs>();
    ComplexRs complexRs = new ComplexRs();
    complexRs.setId(userId);
    result.setData(complexRs);

    return result;
  }

  private Dialog findOrCreateDialog(Long firstPersonId, Long secondPersonId){
    Optional<Dialog> dialogOpt = dialogRepository.findDialogByPersonIds(firstPersonId, secondPersonId);
    if (dialogOpt.isPresent()) return dialogOpt.get();

    Person firstPerson = new Person();
    firstPerson.setId(firstPersonId);
    Person secondPerson = new Person();
    secondPerson.setId(secondPersonId);

    Dialog dialog = new Dialog();
    dialog.setFirstPerson(firstPerson);
    dialog.setSecondPerson(secondPerson);
    Dialog savedDialog = dialogRepository.save(dialog);
    var lastMessage =
        this.createMessage(savedDialog.getId(), firstPerson.getId(), secondPerson.getId(), "Создан диалог");
    savedDialog.setLastMessageId(lastMessage.getId());
    savedDialog.setLastActiveTime(lastMessage.getTime());
    dialogRepository.save(savedDialog);

    return savedDialog;
  }

  @Transactional
  public CommonRs<ComplexRs> setReadDialog(Long dialogId) {
    Dialog dialog = dialogRepository.getReferenceById(dialogId);
    this.setReadMessage(dialog.getLastMessageId());

    var result = new CommonRs<ComplexRs>();
    ComplexRs complexRs = new ComplexRs();
    complexRs.setId(dialogId);
    result.setData(complexRs);
    return result;
  }


  private MessageRs getMessageById(String authorization, Long messageId){
    Long userId = jwtTokenUtils.getId(authorization);
    Optional<Message> messageOpt = messageRepository.findById(messageId);
    if (messageOpt.isEmpty()) throw MessageException.messageNotFound(messageId);

    MessageRs message = MessageMapper.INSTANCE.messageToMessageRs(messageOpt.get());
    message.setIsSentByMe(message.getAuthorId().equals(userId));
    return message;
  }

  private void setReadMessage(Long messageId){
    if (messageId == null) return;

    messageRepository.findById(messageId).ifPresent(message -> {
      message.setReadStatus(ReadStatus.READ);
      messageRepository.save(message);
    });
  }

  private Message createMessage(Long dialogId, Long authorId, Long recipientId,  String messageText){
    Message message = new Message();
    Person author = new Person();
    author.setId(authorId);
    Person recipient = new Person();
    recipient.setId(recipientId);
    Dialog dialog = new Dialog();
    dialog.setId(dialogId);

    message.setDialog(dialog);
    message.setAuthor(author);
    message.setRecipient(recipient);
    message.setReadStatus(ReadStatus.UNREAD);
    message.setTime(LocalDateTime.now());
    message.setMessageText(messageText);

    return messageRepository.save(message);
  }

}
