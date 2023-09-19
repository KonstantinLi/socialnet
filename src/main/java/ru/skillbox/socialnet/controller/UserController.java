package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.service.PersonService;
import ru.skillbox.socialnet.util.JwtTokenUtils;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final PersonService personService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/{id}")
    public CommonRsPersonRs<PersonRs> getUserById (@PathVariable(value = "id") Long id,
                                                                   @RequestHeader("authorization") String token) throws BadRequestException {
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        PersonRs personRs = personService.getUserById(id, jwtTokenUtils.getId(token));
        response.setData(personRs);
        return response;
    }
}
