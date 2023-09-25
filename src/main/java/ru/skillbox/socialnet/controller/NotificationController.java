package ru.skillbox.socialnet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRs;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @GetMapping("/notifications")
    public CommonRs<ComplexRs> getAllNotifications() {
        //TODO remove plug and implement login
        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        commonRs.setData(new ComplexRs());
        return commonRs;
    }
}
