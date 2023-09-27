package ru.skillbox.socialnet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.response.PersonRs;
import ru.skillbox.socialnet.errs.BadRequestException;
import ru.skillbox.socialnet.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<CommonRsPersonRs<PersonRs>> GetUserById (@PathVariable(value = "id") Integer id,
                                                                   @RequestHeader("authorization") String token) {
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        response.setData(new PersonRs());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public CommonRsPersonRs<PersonRs> getUserMe (@RequestHeader("authorization") String token) throws BadRequestException {
        return userService.userMe(token);
    }
}
