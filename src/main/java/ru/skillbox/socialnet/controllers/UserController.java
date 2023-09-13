package ru.skillbox.socialnet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.dto.ComplexRs;
import ru.skillbox.socialnet.data.dto.PersonRs;
import ru.skillbox.socialnet.data.dto.response.CorrectResponse;
import ru.skillbox.socialnet.data.entity.other.UserData;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<CorrectResponse<PersonRs>> getUserById(@PathVariable(value = "id") Integer id,
                                                                 @RequestHeader("authorization") String token) {
        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<CorrectResponse<PersonRs>> findUsers(@PathVariable(value = "id") Integer id,
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

        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<CorrectResponse<PersonRs>> getMyInfo(@RequestHeader("authorization") String token) {
        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<CorrectResponse<PersonRs>> updateUserById(@RequestHeader("authorization") String token,
                                                                    @RequestBody UserData userData) {
        CorrectResponse<PersonRs> response = new CorrectResponse<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<CorrectResponse<ComplexRs>> deleteUserById(@RequestHeader("authorization") String token) {
        CorrectResponse<ComplexRs> response = new CorrectResponse<>();
        List<ComplexRs> data = new ArrayList<>();
        data.add(new ComplexRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/recover")
    public ResponseEntity<CorrectResponse<ComplexRs>> recoverUserInfo(@RequestHeader("authorization") String token) {
        CorrectResponse<ComplexRs> response = new CorrectResponse<>();
        List<ComplexRs> data = new ArrayList<>();
        data.add(new ComplexRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }
}
