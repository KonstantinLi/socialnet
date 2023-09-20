package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.services.UserService;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ValidationUtilsRq validationUtils;
private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<CommonRsPersonRs<PersonRs>> GetUserById (@PathVariable(value = "id") Integer id,
                                                                   @RequestHeader("authorization") String token) {
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setData(new PersonRs());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public CommonRsPersonRs<PersonRs> getUserMe (@RequestParam("authorization") String token) throws CommonException {
        return userService.userMe(token);
    }
}
