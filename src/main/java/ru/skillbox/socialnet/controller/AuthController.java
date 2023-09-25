package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
import ru.skillbox.socialnet.model.LoginRq;
import ru.skillbox.socialnet.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/logout")
    public CommonRsComplexRs<ComplexRs> logout(@RequestHeader String authorization) {
        return authService.logout(authorization);
    }

    @PostMapping("/login")
    public CommonRsPersonRs<PersonRs> login(@RequestBody LoginRq loginRq) throws ExceptionBadRq {
        return authService.login(loginRq);
    }

    @GetMapping("/captcha")
    public CaptchaRs captcha() {
        return authService.captcha();
    }
}
