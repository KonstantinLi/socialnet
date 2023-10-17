package ru.skillbox.socialnet.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.service.NotificationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public CommonRs<List<NotificationRs>> getAllNotifications(
            @RequestParam(defaultValue = "10") Integer itemPerPage,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestHeader(name = "authorization") String token) {

        return notificationService.getAllNotifications(token, itemPerPage, offset);
    }

    @PutMapping("/notifications")
    public CommonRs<List<NotificationRs>> readNotification(
            @RequestParam Long id,
            @RequestParam(defaultValue = "true") Boolean all,
            @RequestHeader(name = "authorization") String token) {

        return notificationService.readNotifications(token, id, all);
    }
}
