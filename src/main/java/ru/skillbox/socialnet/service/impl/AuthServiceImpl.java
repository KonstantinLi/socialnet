package ru.skillbox.socialnet.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skillbox.socialnet.dto.auth.JwtResponse;
import ru.skillbox.socialnet.dto.auth.LoginUser;
import ru.skillbox.socialnet.dto.auth.RegistrationUser;
import ru.skillbox.socialnet.dto.auth.UserResponse;
import ru.skillbox.socialnet.exception.WrongLoginOrPasswordException;
import ru.skillbox.socialnet.service.interfaces.AuthService;
import ru.skillbox.socialnet.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginUser loginUser) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new WrongLoginOrPasswordException();
        }

        UserDetails userDetails = userService.loadUserByUsername(loginUser.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    //TODO: compare passwords and captcha codes, check whether user exists, then save user
    @Override
    public ResponseEntity<UserResponse> register(RegistrationUser registrationUser) {
        return null;
    }
}
