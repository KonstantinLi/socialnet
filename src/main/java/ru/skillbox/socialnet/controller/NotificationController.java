package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.swagger.FullSwaggerDescription;
import ru.skillbox.socialnet.annotation.swagger.Token;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.service.NotificationService;

import java.util.List;

@Tag(name = "NotificationController", description = "Get, read notifications")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @FullSwaggerDescription(summary = "get all notifications for user")
    @GetMapping
    public CommonRs<List<NotificationRs>> getAllNotifications(
            @RequestParam(defaultValue = "false") @Parameter(description = "isRead", example = "false")
            Boolean isRead,
            @RequestParam(defaultValue = "10") @Parameter(description = "itemPerPage", example = "10")
            Integer itemPerPage,
            @RequestParam(defaultValue = "0") @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) @Token String token) {

        return notificationService.getAllNotifications(token, itemPerPage, offset, isRead);
    }

    @FullSwaggerDescription(summary = "read notification")
    @PutMapping
    public CommonRs<List<NotificationRs>> readNotification(
            @RequestParam(defaultValue = "0") @Parameter(description = "id", example = "1")
            Long id,
            @RequestParam(defaultValue = "true") @Parameter(description = "all", example = "false")
            Boolean all,
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) @Token String token) {

        return notificationService.readNotifications(token, id, all);
    }
}