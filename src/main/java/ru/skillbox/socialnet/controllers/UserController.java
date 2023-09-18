package ru.skillbox.socialnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnet.data.dto.ComplexRs;
import ru.skillbox.socialnet.data.dto.PersonRs;
import ru.skillbox.socialnet.data.dto.UserRq;
import ru.skillbox.socialnet.data.dto.response.ApiResponse;
import ru.skillbox.socialnet.data.dto.response.CorrectResponse;
import ru.skillbox.socialnet.data.dto.response.ErrorResponse;
import ru.skillbox.socialnet.service.PersonService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private PersonService personService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable(value = "id") Long id,
                                                   @RequestHeader("authorization") String token) {
        //TODO check if user is authorized to get this info (check token)
        ApiResponse apiResponse = personService.getPersonRsById(id);

        if (apiResponse instanceof ErrorResponse) {
            return ResponseEntity.badRequest().body(apiResponse);
        } else {
            return ResponseEntity.ok(apiResponse);
        }
    }



    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyInfo(@RequestHeader(value = "authorization") String token) {
        //TODO userID should be taken from token
        Long userId = personService.getRandomIdFromDB();

        return getUserById(userId, token);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse> updateMyInfo(@RequestHeader("authorization") String token,
                                                    @RequestBody UserRq userData) {

        //TODO userID should be taken from token
        long userId = personService.getRandomIdFromDB();

        ApiResponse apiResponse = personService.updateUserInfo(userId, userData);

        if (apiResponse instanceof ErrorResponse) {
            return ResponseEntity.badRequest().body(apiResponse);
        } else {
            return ResponseEntity.ok(apiResponse);
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteMyInfo(@RequestHeader("authorization") String token) {

        //TODO userID should be taken from token
        long userId = personService.getRandomIdFromDB();

        ApiResponse apiResponse = personService.deletePersonById(userId);

        if (apiResponse instanceof ErrorResponse) {
            return ResponseEntity.badRequest().body(apiResponse);
        } else {
            return ResponseEntity.ok(apiResponse);
        }
    }

    @PostMapping("/me/recover")
    public ResponseEntity<ApiResponse> recoverUserInfo(@RequestHeader("authorization") String token) {
        //TODO later
        CorrectResponse<ComplexRs> response = new CorrectResponse<>();
        response.setData(List.of(new ComplexRs()));
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

}
