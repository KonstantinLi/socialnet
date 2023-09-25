package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.service.PersonService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private PersonService personService;
    private JwtTokenUtils jwtTokenUtils;

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/login")
    public CommonRs<PersonRs> login() throws BadRequestException {
        //TODO remove plug and implement login
        return personService.getUserById(13L, 13L);
    }
}
