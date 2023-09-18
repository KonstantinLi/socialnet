package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.model.RegistrationInfo;
import ru.skillbox.socialnet.services.AccountService;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> postRegisterRequest(@RequestBody RegistrationInfo registrationInfo) {
        return accountService.registration(registrationInfo);
    }
}
