package ru.skillbox.socialnet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.dto.errs.BadRequestException;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.service.UserService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonRsPersonRs<PersonRs>> GetUserById (@PathVariable(value = "id") Long id,
                                                                   @RequestHeader("authorization") String token) throws BadRequestException {
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        List<PersonRs> data = new ArrayList<>();
        PersonRs personRs = userService.getUserById(id);
        if (personRs == null) {
            throw new BadRequestException("no such person on id");
        }
        data.add(userService.getUserById(id));
        response.setData(data);
        return ResponseEntity.ok(response);
    }
}
