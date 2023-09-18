package ru.skillbox.socialnet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.data.dto.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse> getAllNotifications() {
        //TODO remove plug and implement login
        return ResponseEntity.ok(new ApiResponse());
    }
}
