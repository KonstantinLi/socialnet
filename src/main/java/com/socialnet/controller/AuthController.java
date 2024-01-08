package com.socialnet.controller;

import com.socialnet.annotation.FullSwaggerDescription;
import com.socialnet.annotation.Info;
import com.socialnet.annotation.OnlineStatusUpdate;
import com.socialnet.annotation.Token;
import com.socialnet.dto.request.LoginRq;
import com.socialnet.dto.response.CaptchaRs;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.PersonRs;
import com.socialnet.service.AuthService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Info
@ApiResponse(responseCode = "200")
public class AuthController {

    private final AuthService authService;

    @FullSwaggerDescription(summary = "login by email and password")
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public CommonRs<PersonRs> login(@RequestBody LoginRq loginRq) {

        return authService.login(loginRq);
    }

    @OnlineStatusUpdate
    @FullSwaggerDescription(summary = "logout current user")
    @PostMapping(value = "/logout", produces = "application/json")
    public CommonRs<ComplexRs> logout(@RequestHeader("authorization") @Token String authorization) {

        return authService.logout();
    }

    @FullSwaggerDescription(summary = "get captcha secret code and image url")
    @GetMapping(value = "/captcha", produces = "application/json")
    public CaptchaRs captcha() {

        return authService.captcha();
    }
}
