package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.AwsS3Handler;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final AwsS3Handler awsS3Handler;

    @GetMapping()
    public String test() {

        StringBuilder sb = new StringBuilder();
        awsS3Handler.getLogFilesUrls().forEach(url -> sb.append(url).append("\n"));
        sb.append("\n").append("test complete");

        return sb.toString();
    }
}
