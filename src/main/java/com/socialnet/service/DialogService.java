package com.socialnet.service;

import com.socialnet.dto.request.DialogUserShortListRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.DialogRs;
import com.socialnet.dto.response.MessageRs;
import com.socialnet.entity.enums.ReadStatus;
import com.socialnet.repository.DialogRepository;
import com.socialnet.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.socialnet.entity.dialogrelated.Dialog;
import com.socialnet.entity.dialogrelated.Message;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.exception.MessageException;
import com.socialnet.mapper.DialogMapper;
import com.socialnet.mapper.MessageMapper;
import com.socialnet.security.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DialogService {

    private static final String CREATE_DIALOG = "Создан диалог";

    private final JwtTokenUtils jwtTokenUtils;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final DialogMapper dialogMapper;
    private final MessageMapper messageMapper;


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
        var dialogRs = dialogMapper.dialogToDialogRs(dialog);

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

    private Dialog findOrCreateDialog(Long firstPersonId, Long secondPersonId) {
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
                this.createMessage(savedDialog.getId(), firstPerson.getId(), secondPerson.getId());
        savedDialog.setLastMessageId(lastMessage.getId());
        savedDialog.setLastActiveTime(lastMessage.getTime());
        dialogRepository.save(savedDialog);

        return savedDialog;
    }

    @Transactional
    public CommonRs<ComplexRs> setReadDialog(String authorization, Long dialogId) {
        Long userId = jwtTokenUtils.getId(authorization);
        dialogRepository.findById(dialogId).ifPresent(dialog -> this.setReadMessageByDialog(dialogId, userId));

        var result = new CommonRs<ComplexRs>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setId(dialogId);
        result.setData(complexRs);
        return result;
    }


    private MessageRs getMessageById(String authorization, Long messageId) {
        Long userId = jwtTokenUtils.getId(authorization);
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) throw MessageException.messageNotFound(messageId);

        MessageRs message = messageMapper.messageToMessageRs(messageOpt.get());
        message.setIsSentByMe(message.getAuthorId().equals(userId));
        return message;
    }

    private void setReadMessageByDialog(Long dialogId, Long userId) {
        if (dialogId == null) return;

        messageRepository.getUnreadMessagesByDialogIdAndUserId(dialogId, userId).forEach(message -> {
            message.setReadStatus(ReadStatus.READ);
            messageRepository.save(message);
        });

    }

    private Message createMessage(Long dialogId, Long authorId, Long recipientId) {
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
        message.setMessageText(CREATE_DIALOG);

        return messageRepository.save(message);
    }

}
