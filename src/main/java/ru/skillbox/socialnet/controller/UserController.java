package ru.skillbox.socialnet.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.AuthRequired;
import ru.skillbox.socialnet.annotation.FullSwaggerDescription;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.annotation.Token;
import ru.skillbox.socialnet.dto.parameters.GetUsersSearchPs;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
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

    @FullSwaggerDescription(summary = "get user by id")
    @GetMapping(value = "/{id}", produces = "application/json")
    public CommonRs<PersonRs> getUserById(@PathVariable(value = "id")
                                          @Parameter(description = "id", example = "1", required = true)
                                          Long id,
                                          @RequestHeader("authorization")
                                          @Token
                                          String token) {

        return personService.getUserById(jwtTokenUtils.getId(token), id);
    }

    @FullSwaggerDescription(summary = "get information about me")
    @GetMapping(value = "/me", produces = "application/json")
    public CommonRs<PersonRs> getMyInfo(@RequestHeader(value = "authorization")
                                        @Token
                                        String token) {

        return getUserById(jwtTokenUtils.getId(token), token);
    }

    @ApiResponse(responseCode = "200")
    @AuthRequired(summary = "update information about me")
    @PutMapping(value = "/me", produces = "application/json", consumes = "application/json")
    public CommonRs<PersonRs> updateMyInfo(@RequestHeader("authorization")
                                           @Token
                                           String token,
                                           @RequestBody UserRq userData) {


        return personService.updateUserInfo(jwtTokenUtils.getId(token), userData);
    }

    @ApiResponse(responseCode = "200")
    @AuthRequired(summary = "delete information about me")
    @DeleteMapping(value = "/me", produces = "application/json")
    public CommonRs<ComplexRs> deleteMyInfo(@RequestHeader("authorization")
                                            @Token
                                            String token) {

        return personService.deletePersonById(jwtTokenUtils.getId(token));
    }

    @FullSwaggerDescription(summary = "recover information about me")
    @PostMapping(value = "/me/recover", produces = "application/json")
    public CommonRs<ComplexRs> recoverUserInfo(@RequestHeader("authorization")
                                               @Token
                                               String token) {

        return personService.recoverUserInfo(jwtTokenUtils.getId(token));
    }

    @ApiResponse(responseCode = "200")
    @AuthRequired(summary = "search users by query")
    @GetMapping(value = "/search", produces = "application/json")
    public CommonRs<List<PersonRs>> findUsers(@RequestHeader("authorization")
                                              @Token
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
