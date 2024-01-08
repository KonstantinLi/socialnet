package com.socialnet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.socialnet.annotation.Info;
import com.socialnet.annotation.swagger.BadRequestResponseDescription;
import com.socialnet.annotation.swagger.FullSwaggerDescription;
import com.socialnet.annotation.swagger.Token;
import com.socialnet.dto.request.*;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.PersonSettingsRs;
import com.socialnet.dto.response.RegisterRs;
import com.socialnet.exception.BadRequestException;
import com.socialnet.kafka.KafkaService;
import com.socialnet.service.AccountService;
import com.socialnet.service.PersonSettingsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    private final KafkaService kafkaService;

    @FullSwaggerDescription(summary = "user registration")
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

    @BadRequestResponseDescription(summary = "send email with password recovery link")
    @PutMapping(value = "/password/recovery", consumes = "application/json")
    public void passwordRecovery(@RequestBody PasswordRecoveryRq passwordRecoveryRq) throws JsonProcessingException {

        kafkaService.sendMessage(passwordRecoveryRq);
    }

    @FullSwaggerDescription(summary = "reset user password")
    @PutMapping(value = "/password/reset", consumes = "application/json", produces = "application/json")
    public RegisterRs<ComplexRs> passwordReset(@RequestBody PasswordResetRq passwordResetRq)
            throws BadRequestException {

        return accountService.resetPassword(passwordResetRq);
    }

    @FullSwaggerDescription(summary = "change user email")
    @PutMapping(value = "/email/recovery")
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
    @GetMapping(value = "/notifications", produces = "application/json")
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
