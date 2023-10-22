package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.ErrorAPIResponsesDescription;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.annotation.OkAPIResponseDescription;
import ru.skillbox.socialnet.annotation.ParameterDescription;
import ru.skillbox.socialnet.dto.request.*;
import ru.skillbox.socialnet.dto.response.*;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.service.AccountService;
import ru.skillbox.socialnet.service.PersonSettingsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Info
@ErrorAPIResponsesDescription
public class AccountController {

    private final AccountService accountService;
    private final PersonSettingsService personSettingsService;

    @OkAPIResponseDescription(summary = "register new user", value = "RegisterRsComplexRs")
    @PostMapping("/register")
    public RegisterRs<ComplexRs> register(
            @RequestBody @ParameterDescription(description = "register request", implementation = RegisterRq.class)
                                          RegisterRq registerRq) {

        return accountService.registration(registerRq);
    }

    @Operation(summary = "set/change user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer
                                    ref = "#/components/schemas/RegisterRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content)})
    @PutMapping("/password/set")
//TODO should there be authorization token also?
    public RegisterRs<ComplexRs> setPassword(
            @RequestBody @Parameter(
                    description = "password set request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(description = "new password",
                                    implementation = PasswordSetRq.class))
            ) PasswordSetRq passwordSetRq)
            throws BadRequestException {

        return accountService.setPassword(passwordSetRq);
    }

    @Operation(summary = "send email with password recovery link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/password/recovery")
    public void passwordRecovery(@RequestBody @Parameter(
            description = "password recovery request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(description = "user email address for password recovery",
                            implementation = PasswordRecoveryRq.class))
    ) PasswordRecoveryRq passwordRecoveryRq) {

        accountService.passwordRecovery(passwordRecoveryRq);
    }

    @Operation(summary = "change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer?
                                    ref = "#/components/schemas/RegisterRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/password/reset")
    public RegisterRs<ComplexRs> passwordReset(@RequestBody @Parameter(
            description = "password set request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(description = "new password and authorization token",
                            implementation = PasswordResetRq.class))
    ) PasswordResetRq passwordResetRq)
            throws BadRequestException {

        return accountService.resetPassword(passwordResetRq);
    }

    @Operation(summary = "send email with email recovery link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/email/recovery")
    public void emailRecovery(
            @RequestHeader("authorization") @Parameter(description = "access token", example = "JWT Token",
                    required = true) String token,
            @RequestBody @Parameter(description = "user address to send recovery link",
                    example = "obivan.k@rmail.com") String email) {

        accountService.emailRecovery(token, email);
    }

    @Operation(summary = "set new email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer?
                                    ref = "#/components/schemas/RegisterRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/email")
    public RegisterRs<ComplexRs> setNewEmail(@RequestBody @Parameter(
            description = "email set request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(description = "new email",
                            implementation = EmailRq.class))
    ) EmailRq emailRq) {

        return accountService.setEmail(emailRq);
    }

    @Operation(summary = "get user's notifications settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer?
                                    ref = "#/components/schemas/CommonRsListPersonSettingsRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @GetMapping("/notifications")
    public CommonRs<List<PersonSettingsRs>> getSettings(
            @RequestHeader("Authorization") @Parameter(description = "access token", example = "JWT Token",
                    required = true) String token) {
        return personSettingsService.getPersonSettings(token);
    }

    @Operation(summary = "edit user's notifications settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(
                                    description = "default response from server",
                                    //TODO check what is the correct answer?
                                    ref = "#/components/schemas/CommonRsComplexRs")
                    )}),
            @ApiResponse(responseCode = "400", description = "name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(description = "common error response", implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PutMapping("/notifications")
    public CommonRs<ComplexRs> editSettings(
            @RequestHeader("Authorization") @Parameter(description = "access token", example = "JWT Token",
                    required = true) String token,
            @RequestBody @Parameter(description = "edit settings request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(description = "new settings",
                                    implementation = PersonSettingsRq.class)))
            PersonSettingsRq personSettingsRq) {

        return personSettingsService.editPersonSettings(token, personSettingsRq);
    }
}
