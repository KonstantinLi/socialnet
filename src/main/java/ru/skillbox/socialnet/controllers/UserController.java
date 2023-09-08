package ru.skillbox.socialnet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.CorrectResponse;
import ru.skillbox.socialnet.data.dto.PersonRs;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<CorrectResponse<PersonRs>> GetUserById (@PathVariable(value = "id") Integer id,
                                                                  @RequestHeader("authorization") String token) {
        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }
}
