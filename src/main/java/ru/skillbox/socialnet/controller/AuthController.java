package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.FullSwaggerDescription;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.annotation.OnlineStatusUpdate;
import ru.skillbox.socialnet.annotation.Token;
import ru.skillbox.socialnet.dto.request.LoginRq;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Info
@ApiResponse(responseCode = "200")
public class AuthController {

    private final AuthService authService;

    @FullSwaggerDescription(summary = "login by email and password")
    @PostMapping("/login")
    public CommonRs<PersonRs> login(@RequestBody LoginRq loginRq) {

        return authService.login(loginRq);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "logout current user")
    @PostMapping("/logout")
    public CommonRs<ComplexRs> logout(@RequestHeader("authorization") @Token String authorization) {

        return authService.logout();
    }

    @FullSwaggerDescription(summary = "get captcha secret code and image url")
    @GetMapping("/captcha")
    public CaptchaRs captcha() {

        return authService.captcha();
    }
}
