package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.request.LoginRq;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public CommonRs<PersonRs> login(@RequestBody LoginRq loginRq) {

        return authService.login(loginRq);
    }

    @PostMapping("/logout")
    public CommonRs<ComplexRs> logout(@RequestHeader String authorization) {

        return authService.logout(authorization);
    }

    @GetMapping("/captcha")
    public CaptchaRs captcha() {

        return authService.captcha();
    }
}
