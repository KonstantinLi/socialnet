package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/api/v1/dialogs")
public class DialogsController {

    private final DialogService dialogService;
    private final MessageService messageService;


    @PutMapping("/{dialogId}/read")
    public CommonRs<ComplexRs> setReadDialog(@RequestHeader(name = "authorization") String authorization,
                                             @PathVariable Long dialogId) {
        return dialogService.setReadDialog(authorization, dialogId);
    }

    @GetMapping("")
    public CommonRs<List<DialogRs>> getDialogs(@RequestHeader(name = "authorization") String authorization) {
        return dialogService.getDialogs(authorization);
    }

    @PostMapping("")
    public CommonRs<ComplexRs> startDialog(@RequestHeader(name = "authorization") String authorization,
                                           @RequestBody DialogUserShortListRq dialogUserShortListDto) {
        return dialogService.startDialog(authorization, dialogUserShortListDto);
    }


    @GetMapping("/unreaded")
    public CommonRs<ComplexRs> getUnreadedDialogs(@RequestHeader(name = "authorization") String authorization) {
        return messageService.getCountUnreadedMessages(authorization);
    }

    @GetMapping("/{dialogId}/messages")
    public CommonRs<List<MessageRs>> getMessageFromDialog(@RequestHeader(name = "authorization") String authorization,
                                                          @PathVariable Long dialogId,
                                                          @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                          @RequestParam(name = "perPage", required = false, defaultValue = "20") int perPage) {

        return messageService.getMessagesByDialog(authorization, dialogId, offset, perPage);
    }
}
