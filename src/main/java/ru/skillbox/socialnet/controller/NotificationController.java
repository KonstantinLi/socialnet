package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public CommonRs<List<NotificationRs>> getAllNotifications(
            @RequestParam(defaultValue = "10") Integer itemPerPage,
            @RequestParam(defaultValue = "0") Integer offset,
            @Header("Authorization") String token) {

        return notificationService.getAllNotifications(token, itemPerPage, offset);
    }

    @PutMapping("/notifications")
    public CommonRs<List<NotificationRs>> readNotification(
            @RequestParam Long id,
            @RequestParam(defaultValue = "true") Boolean all,
            @Header("Authorization") String token) {

        return notificationService.readNotifications(token, id, all);
    }
}
