package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.dto.parameters.GetUsersSearchPs;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.ErrorRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.service.PersonService;

import java.util.List;


@Tag(name = "UsersController", description = "Get user. Get, update, delete, recover personal info. User search")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Info
public class UserController {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    @Operation(summary = "get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @GetMapping("/{id}")
    public CommonRs<PersonRs> getUserById(@PathVariable(value = "id")
                                          @Parameter(description = "id", example = "1", required = true)
                                          Long id,
                                          @RequestHeader("authorization")
                                          @Parameter(description = "Access Token", example = "JWT Token")
                                          String token) {

        return personService.getUserById(jwtTokenUtils.getId(token), id);
    }

    @Operation(summary = "get information about me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @GetMapping("/me")
    public CommonRs<PersonRs> getMyInfo(@RequestHeader(value = "authorization")
                                        @Parameter(description = "Access Token", example = "JWT Token")
                                        String token) {

        return getUserById(jwtTokenUtils.getId(token), token);
    }

    @Operation(summary = "update information about me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsPersonRs"))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @PutMapping("/me")
    public CommonRs<PersonRs> updateMyInfo(@RequestHeader("authorization")
                                           @Parameter(description = "Access Token", example = "JWT Token")
                                           String token,
                                           @RequestBody @Parameter(description = "user data",
                                                   examples = @ExampleObject(value = """
                                                           {
                                                             "about": "string",
                                                             "city": "string",
                                                             "country": "string",
                                                             "phone": "string",
                                                             "birth_date": "string",
                                                             "first_name": "string",
                                                             "last_name": "string",
                                                             "messages_permission": "string",
                                                             "photo_id": "string"
                                                           }
                                                           """)) UserRq userData) {


        return personService.updateUserInfo(jwtTokenUtils.getId(token), userData);
    }

    @Operation(summary = "delete information about me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @DeleteMapping("/me")
    public CommonRs<ComplexRs> deleteMyInfo(@RequestHeader("authorization")
                                            @Parameter(description = "Access Token", example = "JWT Token")
                                            String token) {

        return personService.deletePersonById(jwtTokenUtils.getId(token));
    }

    @Operation(summary = "recover information about me")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @PostMapping("/me/recover")
    public CommonRs<ComplexRs> recoverUserInfo(@RequestHeader("authorization") String token) {

        return personService.recoverUserInfo(jwtTokenUtils.getId(token));
    }

    @Operation(summary = "search users by query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(ref = "#/components/schemas/CommonRsListPersonRs"))}),
            @ApiResponse(responseCode = "401", description =  "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description =  "Forbidden", content = @Content)
    })
    @GetMapping("/search")
    public CommonRs<List<PersonRs>> findUsers(@RequestHeader("authorization")
                                              @Parameter(description = "Access Token", example = "JWT Token")
                                              String token,
                                              @RequestParam(value = "age_from",
                                                      required = false, defaultValue = "0")
                                              @Parameter(description = "after this age", example = "5") int ageFrom,
                                              @RequestParam(value = "age_to",
                                                      required = false, defaultValue = "0")
                                              @Parameter(description = "before this age", example = "50") int ageTo,
                                              @RequestParam(value = "city",
                                                      required = false)
                                              @Parameter(description = "city name", example = "Paris") String city,
                                              @RequestParam(value = "country",
                                                      required = false)
                                              @Parameter(description = "country name", example = "France") String country,
                                              @RequestParam(value = "first_name",
                                                      required = false)
                                              @Parameter(description = "possible first name", example = "Максим") String firstName,
                                              @RequestParam(value = "last_name",
                                                      required = false)
                                              @Parameter(description = "possible last name", example = "Иванов") String lastName,
                                              @RequestParam(value = "offset",
                                                      required = false, defaultValue = "0")
                                              @Parameter(description = "offset", example = "0") int offset,
                                              @RequestParam(value = "perPage",
                                                      required = false, defaultValue = "20")
                                              @Parameter(description = "per page", example = "20") int perPage) {

        return personService.getUsersByQuery(jwtTokenUtils.getId(token),
                GetUsersSearchPs.builder()
                        .ageFrom(ageFrom)
                        .ageTo(ageTo)
                        .city(city)
                        .country(country)
                        .firstName(firstName)
                        .lastName(lastName)
                        .build(),
                offset,
                perPage);
    }
}
