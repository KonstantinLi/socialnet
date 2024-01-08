package com.socialnet.mapper;

import com.socialnet.dto.response.DialogRs;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.socialnet.entity.dialogrelated.Dialog;

@Mapper(componentModel = "spring")
public interface DialogMapper {

    @Mapping(target = "authorId", source = "firstPerson.id")
    @Mapping(target = "recipientId", source = "secondPerson.id")
    DialogRs dialogToDialogRs(Dialog dialog);

}
