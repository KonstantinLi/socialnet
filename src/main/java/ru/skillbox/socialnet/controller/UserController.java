package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.ComplexRs;
import ru.skillbox.socialnet.dto.UserRq;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.service.PersonService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/{id}")
    public CommonRs<PersonRs> getUserById(@PathVariable(value = "id") Long id,
                                          @RequestHeader("authorization") String token)
            throws BadRequestException {
        return personService.getUserById(id, jwtTokenUtils.getId(token));
    }

    @GetMapping("/me")
    public CommonRs<PersonRs> getMyInfo(@RequestHeader(value = "authorization") String token)
            throws BadRequestException {
        //TODO userID should be taken from token
        Long userId = 13L;

        return getUserById(userId, token);
    }

    @PutMapping("/me")
    public CommonRs<PersonRs> updateMyInfo(@RequestHeader("authorization") String token,
                                                    @RequestBody UserRq userData) {

        //TODO userID should be taken from token
        long userId = 13L;

        return personService.updateUserInfo(userId, userData);
    }

    @DeleteMapping("/me")
    public CommonRs<ComplexRs> deleteMyInfo(@RequestHeader("authorization") String token) {
        Long userId = jwtTokenUtils.getId(token);

        return personService.deletePersonById(userId);
    }

    @PostMapping("/me/recover")
    public CommonRs<PersonRs> recoverUserInfo(@RequestHeader("authorization") String token) {
        //TODO later
        CommonRs<PersonRs> response = new CommonRs<>();
        response.setData(new PersonRs());
        return response;
    }

    @GetMapping("/search")
    public CommonRs<List<PersonRs>> findUsers(@PathVariable(value = "id") Integer id,
                                                               @RequestHeader("authorization") String token,
                                                               @RequestParam(value = "age_from", required = false, defaultValue = "0") int ageFrom,
                                                               @RequestParam(value = "age_to", required = false, defaultValue = "0") int ageTo,
                                                               @RequestParam(value = "city", required = false) String city,
                                                               @RequestParam(value = "country", required = false) String country,
                                                               @RequestParam(value = "first_name", required = false) String firstName,
                                                               @RequestParam(value = "last_name", required = false) String lastName,
                                                               @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
                                                               @RequestParam(value = "perPage", required = false, defaultValue = "20") int perPage
    ) {

        CommonRs<List<PersonRs>> response = new CommonRs<>();
        ArrayList<PersonRs> persons = new ArrayList<>();
        response.setData(persons);
        return response;
    }

}
