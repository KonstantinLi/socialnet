package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.AwsS3Handler;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AwsS3Handler awsS3Handler;

    @GetMapping()
    public String test() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

}
