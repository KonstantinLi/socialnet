package ru.skillbox.socialnet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnet.data.dto.response.ApiResponse;
import ru.skillbox.socialnet.service.PersonService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private PersonService personService;

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login() {
        //TODO remove plug and implement login
        Long userId = personService.getRandomIdFromDB();
        ApiResponse apiResponse = personService.getPersonRsById(1L);
        return ResponseEntity.ok(apiResponse);
    }
}
