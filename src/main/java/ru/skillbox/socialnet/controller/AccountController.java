package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.dto.request.*;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonSettingsRs;
import ru.skillbox.socialnet.dto.response.RegisterRs;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.service.AccountService;
import ru.skillbox.socialnet.service.PersonSettingsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Info
public class AccountController {

    private final AccountService accountService;
    private final PersonSettingsService personSettingsService;

    @PostMapping("/register")
    public RegisterRs<ComplexRs> register(@RequestBody RegisterRq registerRq) {

        return accountService.registration(registerRq);
    }

    @PutMapping("/password/set")
    public RegisterRs<ComplexRs> setPassword(@RequestBody PasswordSetRq passwordSetRq)
            throws BadRequestException {

        return accountService.setPassword(passwordSetRq);
    }

    @PutMapping("/password/recovery")
    public void passwordRecovery(@RequestBody PasswordRecoveryRq passwordRecoveryRq) {

        accountService.passwordRecovery(passwordRecoveryRq);
    }

    @PutMapping("/password/reset")
    public RegisterRs<ComplexRs> passwordReset(@RequestBody PasswordRq passwordSetRq)
            throws BadRequestException {

        return accountService.resetPassword(passwordSetRq);
    }

    @PutMapping("/email/recovery")
    public void emailRecovery(@RequestHeader("Authorization") String token,
                              @RequestBody String email) {

        accountService.emailRecovery(token, email);
    }

    @PutMapping("/email")
    public RegisterRs<ComplexRs> setNewEmail(@RequestBody EmailRq emailRq) {

        return accountService.setEmail(emailRq);
    }

    @GetMapping("/notifications")
    public CommonRs<List<PersonSettingsRs>> getSettings(@RequestHeader("Authorization") String token) {
        return personSettingsService.getPersonSettings(token);
    }

    @PutMapping("/notifications")
    public CommonRs<ComplexRs> editSettings(
            @RequestHeader("Authorization") String token,
            @RequestBody PersonSettingsRq personSettingsRq) {

        return personSettingsService.editPersonSettings(token, personSettingsRq);
    }
}
