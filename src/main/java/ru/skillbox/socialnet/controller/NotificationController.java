package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.dto.response.NotificationRs;
import ru.skillbox.socialnet.service.NotificationService;

import java.util.List;

@Tag(name = "NotificationController", description = "Get, read notifications")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "get all notifications for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListNotificationRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)})
    @GetMapping("/notifications")
    public CommonRs<List<NotificationRs>> getAllNotifications(
            @RequestParam(defaultValue = "10") @Parameter(description = "itemPerPage", example = "10")
            Integer itemPerPage,
            @RequestParam(defaultValue = "0") @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) String token) {

        return notificationService.getAllNotifications(token, itemPerPage, offset);
    }

    @Operation(summary = "read notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    ref = "#/components/schemas/CommonRsListNotificationRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)})
    @PutMapping("/notifications")
    public CommonRs<List<NotificationRs>> readNotification(
            @RequestParam(defaultValue = "0") @Parameter(description = "id", example = "1")
            Long id,
            @RequestParam(defaultValue = "true") @Parameter(description = "all", example = "false")
            Boolean all,
            @RequestHeader(name = "authorization") @Parameter(description = "Access Token", example = "JWT Token",
                    required = true) String token) {

        return notificationService.readNotifications(token, id, all);
    }
}