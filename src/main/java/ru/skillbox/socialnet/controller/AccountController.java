package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.annotation.swagger.BadRequestResponseDescription;
import ru.skillbox.socialnet.annotation.swagger.FullSwaggerDescription;
import ru.skillbox.socialnet.annotation.swagger.Token;
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
@Tag(name = "AccountController",
        description = "User registration, password reset/recovery, email change, notifications settings")
@ApiResponse(responseCode = "200")
public class AccountController {

    private final AccountService accountService;
    private final PersonSettingsService personSettingsService;

    @BadRequestResponseDescription(summary = "register new user")
    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    public RegisterRs<ComplexRs> register(@RequestBody RegisterRq registerRq) {

        return accountService.registration(registerRq);
    }

    @FullSwaggerDescription(summary = "set user password")
    @PutMapping(value = "/password/set", consumes = "application/json", produces = "application/json")
    public RegisterRs<ComplexRs> setPassword(@RequestBody PasswordSetRq passwordSetRq)
            throws BadRequestException {

        return accountService.setPassword(passwordSetRq);
    }

    @BadRequestResponseDescription
    @Operation(summary = "send email with password recovery link")
    @PutMapping(value = "/password/recovery", produces = "application/json", consumes = "application/json")
    public void passwordRecovery(@RequestBody PasswordRecoveryRq passwordRecoveryRq) {

        accountService.passwordRecovery(passwordRecoveryRq);
    }

    @FullSwaggerDescription(summary = "reset user password")
    @PutMapping(value = "/password/reset", consumes = "application/json", produces = "application/json")
    public RegisterRs<ComplexRs> passwordReset(@RequestBody PasswordResetRq passwordResetRq)
            throws BadRequestException {

        return accountService.resetPassword(passwordResetRq);
    }

    @FullSwaggerDescription(summary = "change user email")
    @PutMapping(value = "/email/recovery", consumes = "text/plain", produces = "application/json")
    public void emailRecovery(
            @RequestHeader("authorization") @Token String token,
            @RequestBody @Parameter(description = "user email to send recovery link"/*, example = "obivan.k@rmail.com"*/)
            String email) {

        accountService.emailRecovery(token, email);
    }

    @FullSwaggerDescription(summary = "set user email")
    @PutMapping(value = "/email", consumes = "application/json", produces = "application/json")
    public RegisterRs<ComplexRs> setNewEmail(@RequestBody EmailRq emailRq) {

        return accountService.setEmail(emailRq);
    }

    @FullSwaggerDescription(summary = "get user's notifications settings")
    @GetMapping(value = "/notifications", produces = "application/json", consumes = "application/json")
    public CommonRs<List<PersonSettingsRs>> getSettings(
            @RequestHeader("Authorization") @Token String token) {

        return personSettingsService.getPersonSettings(token);
    }

    @FullSwaggerDescription(summary = "edit user's notifications settings")
    @PutMapping(value = "/notifications", consumes = "application/json", produces = "application/json")
    public CommonRs<ComplexRs> editSettings(@RequestHeader("authorization") @Token String token,
                                            @RequestBody PersonSettingsRq personSettingsRq) {

        return personSettingsService.editPersonSettings(token, personSettingsRq);
    }
}
