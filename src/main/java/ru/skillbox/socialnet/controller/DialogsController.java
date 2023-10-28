package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.FullSwaggerDescription;
import ru.skillbox.socialnet.annotation.Token;
import ru.skillbox.socialnet.dto.request.DialogUserShortListRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.DialogRs;
import ru.skillbox.socialnet.dto.response.MessageRs;
import ru.skillbox.socialnet.service.DialogService;
import ru.skillbox.socialnet.service.MessageService;

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
    @GetMapping(consumes = "application/json", produces = "application/json")
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
    @GetMapping(value = "/unreaded", consumes = "application/json", produces = "application/json")
    public CommonRs<ComplexRs> getUnreadedDialogs(@RequestHeader(name = "authorization") @Token String authorization) {
        return messageService.getCountUnreadedMessages(authorization);
    }

    @FullSwaggerDescription(summary = "get messages from dialog")
    @GetMapping(value = "/{dialogId}/messages", consumes = "application/json", produces = "application/json")
    public CommonRs<List<MessageRs>> getMessageFromDialog(@RequestHeader(name = "authorization") @Token String authorization,
                                                          @PathVariable Long dialogId,
                                                          @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                          @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {

        return messageService.getMessagesByDialog(authorization, dialogId, offset, perPage);
    }
}
