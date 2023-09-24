package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.RegisterRs;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
import ru.skillbox.socialnet.model.RegisterRq;
import ru.skillbox.socialnet.services.AccountService;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register")
    public RegisterRs<ComplexRs> register(@RequestBody RegisterRq registerRq) throws ExceptionBadRq {
        return accountService.registration(registerRq);
    }
}
