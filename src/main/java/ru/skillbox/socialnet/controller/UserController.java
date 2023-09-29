package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.service.GetUsersSearchPs;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.service.PersonService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/{id}")
    public CommonRs<PersonRs> getUserById(@PathVariable(value = "id") Long id) throws BadRequestException {
        return personService.getUserById(id);
    }

    @GetMapping("/search")
    public CommonRs<List<PersonRs>> searchUsersByQuery(@RequestHeader("authorization") String token,
                                                       @RequestParam(value = "age_from",
                                                               required = false, defaultValue = "0") Integer ageFrom,
                                                       @RequestParam(value = "age_to",
                                                               required = false, defaultValue = "0") Integer ageTo,
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
