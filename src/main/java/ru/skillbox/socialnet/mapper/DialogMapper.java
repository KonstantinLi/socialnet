package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.socialnet.dto.response.DialogRs;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;

@Mapper(componentModel = "spring")
public interface DialogMapper {

  @Mapping(target = "authorId", source = "firstPerson.id")
  @Mapping(target = "recipientId", source = "secondPerson.id")
  DialogRs dialogToDialogRs(Dialog dialog);

}
