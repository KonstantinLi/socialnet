package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.dto.request.LoginRq;
import ru.skillbox.socialnet.exception.AuthException;
import ru.skillbox.socialnet.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/logout")
    public CommonRs<ComplexRs> logout(@RequestHeader String authorization) {
        return authService.logout(authorization);
    }

    @PostMapping("/login")
    public CommonRsPersonRs<PersonRs> login(@RequestBody LoginRq loginRq) throws AuthException {
        return authService.login(loginRq);
    }

    @GetMapping("/captcha")
    public CaptchaRs captcha() {
        return authService.captcha();
    }
}
