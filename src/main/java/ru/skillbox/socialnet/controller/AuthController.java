package ru.skillbox.socialnet.controller;

//import com.github.cage.GCage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.auth.LoginUser;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CaptchaRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.model.LoginInfo;
import ru.skillbox.socialnet.service.impl.AuthServiceImpl;
import ru.skillbox.socialnet.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/logout")
    public CommonRsComplexRs<ComplexRs> logout(@RequestParam String authorization) throws CommonException {
        return authService.logout(authorization);
    }

    @PostMapping("/login")
    public CommonRsPersonRs<PersonRs> login(@RequestBody LoginInfo loginInfo) throws CommonException {
        return authService.login(loginInfo);
//        return null;
    }

    @GetMapping("/captcha")
    public CaptchaRs captcha() {
        return authService.captcha();
    }
}
