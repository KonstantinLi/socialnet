package com.socialnet.controller;

import com.socialnet.annotation.FullSwaggerDescription;
import com.socialnet.dto.request.DialogUserShortListRq;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.DialogRs;
import com.socialnet.dto.response.MessageRs;
import com.socialnet.service.DialogService;
import com.socialnet.service.MessageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.socialnet.annotation.Token;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "DialogsController",
        description = "Get dialogs, start dialog, get read and unread messages")
@ApiResponse(responseCode = "200")
@RequestMapping("/api/v1/dialogs")
public class DialogsController {

    private final DialogService dialogService;
    private final MessageService messageService;


    @FullSwaggerDescription(summary = "read messages in dialog")
    @PutMapping(value = "/{dialogId}/read", consumes = "application/json", produces = "application/json")
    public CommonRs<ComplexRs> setReadDialog(@RequestHeader(name = "authorization") @Token String authorization,
                                             @PathVariable Long dialogId) {
        return dialogService.setReadDialog(authorization, dialogId);
    }

    @FullSwaggerDescription(summary = "get dialogs by user")
    @GetMapping(produces = "application/json")
    public CommonRs<List<DialogRs>> getDialogs(@RequestHeader(name = "authorization") @Token String authorization) {
        return dialogService.getDialogs(authorization);
    }

    @FullSwaggerDescription(summary = "start dialog with user")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public CommonRs<ComplexRs> startDialog(@RequestHeader(name = "authorization") @Token String authorization,
                                           @RequestBody DialogUserShortListRq dialogUserShortListDto) {
        return dialogService.startDialog(authorization, dialogUserShortListDto);
    }

    @FullSwaggerDescription(summary = "get count of unread messages")
    @GetMapping(value = "/unreaded", produces = "application/json")
    public CommonRs<ComplexRs> getUnreadedDialogs(@RequestHeader(name = "authorization") @Token String authorization) {
        return messageService.getCountUnreadedMessages(authorization);
    }

    @FullSwaggerDescription(summary = "get messages from dialog")
    @GetMapping(value = "/{dialogId}/messages", produces = "application/json")
    public CommonRs<List<MessageRs>> getMessageFromDialog(@RequestHeader(name = "authorization") @Token String authorization,
                                                          @PathVariable Long dialogId,
                                                          @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                          @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {

        return messageService.getMessagesByDialog(authorization, dialogId, offset, perPage);
    }

    @FullSwaggerDescription(summary = "get unread messages from dialogs")
    @GetMapping(value = "/{dialogId}/unread")
    public CommonRs<List<MessageRs>> getUnreadMessagesFromDialog(@RequestHeader(name = "authorization") @Token String authorization,
                                                                 @PathVariable Long dialogId) {

        return messageService.getUnreadMessages(authorization, dialogId);
    }
}
