package ru.skillbox.socialnet.controller;

//import com.github.cage.GCage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CaptchaRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.model.LoginInfo;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.services.AccountService;
import ru.skillbox.socialnet.services.AuthService;
import ru.skillbox.socialnet.utils.JwtTokenUtils;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<CommonRsComplexRs<PersonRs>> postLogoutRequest(@RequestParam String authorization) {
        return authService.logout(authorization);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonRsPersonRs<PersonRs>> postLoginRequest(@RequestBody LoginInfo loginInfo) {
        return authService.login(loginInfo);
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaRs> getCaptchaRequest() {
        return authService.captcha();
    }
}
