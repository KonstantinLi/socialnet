package ru.skillbox.socialnet.service.interfaces;

import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnet.dto.auth.JwtResponse;
import ru.skillbox.socialnet.dto.auth.LoginUser;
import ru.skillbox.socialnet.dto.auth.RegistrationUser;
import ru.skillbox.socialnet.dto.auth.UserResponse;

public interface AuthService {
    ResponseEntity<JwtResponse> login(LoginUser loginUser);
    ResponseEntity<UserResponse> register(RegistrationUser registrationUser);
}
