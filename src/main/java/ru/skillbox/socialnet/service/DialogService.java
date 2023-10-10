package ru.skillbox.socialnet.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.DialogUserShortListRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.DialogRs;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;
import ru.skillbox.socialnet.entity.personrelated.Person;
import ru.skillbox.socialnet.mapper.DialogMapper;
import ru.skillbox.socialnet.repository.DialogRepository;
import ru.skillbox.socialnet.security.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class DialogService {

  private final JwtTokenUtils jwtTokenUtils;
  private final DialogRepository dialogRepository;
  private final MessageService messageService;
  private final DialogMapper dialogMapper;

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
      var lastMessage = messageService.getMessageById(authorization, dialog.getLastMessageId());
      dialogRs.setLastMessage(lastMessage);
      dialogRs.setReadStatus(lastMessage.getReadStatus());
    } else { // поищем в сообщениях по диалогу
      var lastMessage = messageService.getLastMessageByDialogId(authorization, dialog.getId());
      if (lastMessage != null){
        dialogRs.setLastMessage(lastMessage);
        dialogRs.setReadStatus(lastMessage.getReadStatus());
      }
    }
    return dialogRs;
  }


  public CommonRs<ComplexRs> startDialog(String authorization, DialogUserShortListRq dialogListRq) {
    Long userId = jwtTokenUtils.getId(authorization);

    List<Dialog> dialogs = dialogListRq.getUserIds()
                                       .stream().map(personId -> findOrCreateDialog(userId, personId))
                                       .toList();


    var result = new CommonRs<ComplexRs>();
    // старт диалога - если диалога нет еще - то создаем его и возвращаем его id
    // диалог есть, то возвращаем последннее сообщение и общее колво сообщений

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
    return dialogRepository.save(dialog);
  }

}
