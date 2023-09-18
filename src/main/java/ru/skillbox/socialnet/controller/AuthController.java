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
import ru.skillbox.socialnet.model.LoginInfo;
import ru.skillbox.socialnet.service.impl.AuthServiceImpl;
import ru.skillbox.socialnet.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
//    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/logout")
    public ResponseEntity<CommonRsComplexRs<PersonRs>> postLogoutRequest(@RequestParam String authorization) {
        return authService.logout(authorization);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonRsPersonRs<PersonRs>> postLoginRequest(@RequestBody LoginInfo loginInfo) {
        return authService.login(loginInfo);
    }

//    @PostMapping("/login")
//    public ResponseEntity<CommonRsPersonRs<PersonRs>> postLoginRequest(@RequestBody LoginUser loginUser) {
//        return authServiceImpl.login(loginUser);
//    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaRs> getCaptchaRequest() {
        return authService.captcha();
    }
}
