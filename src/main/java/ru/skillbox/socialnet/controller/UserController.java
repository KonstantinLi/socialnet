package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.annotation.Info;
import ru.skillbox.socialnet.dto.parameters.GetUsersSearchPs;
import ru.skillbox.socialnet.dto.request.UserRq;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.security.JwtTokenUtils;
import ru.skillbox.socialnet.service.PersonService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Info
public class UserController {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/{id}")
    public CommonRs<PersonRs> getUserById(@PathVariable(value = "id") Long id,
                                          @RequestHeader("authorization") String token) {

        return personService.getUserById(jwtTokenUtils.getId(token), id);
    }

    @GetMapping("/me")
    public CommonRs<PersonRs> getMyInfo(@RequestHeader(value = "authorization") String token) {

        return getUserById(jwtTokenUtils.getId(token), token);
    }

    @PutMapping("/me")
    public CommonRs<PersonRs> updateMyInfo(@RequestHeader("authorization") String token,
                                           @RequestBody UserRq userData) {


        return personService.updateUserInfo(jwtTokenUtils.getId(token), userData);
    }

    @DeleteMapping("/me")
    public CommonRs<ComplexRs> deleteMyInfo(@RequestHeader("authorization") String token) {

        return personService.deletePersonById(jwtTokenUtils.getId(token));
    }

    @PostMapping("/me/recover")
    public CommonRs<PersonRs> recoverUserInfo(@RequestHeader("authorization") String token) {
        //TODO later
        CommonRs<PersonRs> response = new CommonRs<>();
        response.setData(new PersonRs());

        return response;
    }

    @GetMapping("/search")
    public CommonRs<List<PersonRs>> findUsers(@RequestHeader("authorization") String token,
                                              @RequestParam(value = "age_from",
                                                      required = false, defaultValue = "0") int ageFrom,
                                              @RequestParam(value = "age_to",
                                                      required = false, defaultValue = "0") int ageTo,
                                              @RequestParam(value = "city",
                                                      required = false) String city,
                                              @RequestParam(value = "country",
                                                      required = false) String country,
                                              @RequestParam(value = "first_name",
                                                      required = false) String firstName,
                                              @RequestParam(value = "last_name",
                                                      required = false) String lastName,
                                              @RequestParam(value = "offset",
                                                      required = false, defaultValue = "0") int offset,
                                              @RequestParam(value = "perPage",
                                                      required = false, defaultValue = "20") int perPage) {

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
