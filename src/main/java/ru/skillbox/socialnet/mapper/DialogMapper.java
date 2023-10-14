package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.DialogRs;
import ru.skillbox.socialnet.entity.dialogrelated.Dialog;

@Mapper(componentModel = "spring")
public interface DialogMapper {

  DialogMapper INSTANCE = Mappers.getMapper(DialogMapper.class);

  @Mapping(target = "authorId", source = "firstPerson.id")
  @Mapping(target = "recipientId", source = "secondPerson.id")
  DialogRs dialogToDialogRs(Dialog dialog);

}
