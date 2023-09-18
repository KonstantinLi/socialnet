
package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CaptchaRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.model.LoginInfo;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.JwtTokenUtils;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final ValidationUtilsRq validationUtils;
    private final AccountService accountService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userService;
    private final PersonRepository personRepository;

    public ResponseEntity<CommonRsComplexRs<PersonRs>> logout(String authorization) {
        Person person = null;
        try {
//            person = personRsRepository.findByСhangePasswordToken(authorization);
        } catch (BadCredentialsException ex) {
            validationUtils.validationAuthorization(person);
        }
        CommonRsComplexRs<PersonRs> commonRsComplexRs = new CommonRsComplexRs<>();
        commonRsComplexRs.setData(getDataPersonRs(person, getToken(person)));
        return ResponseEntity.ok(commonRsComplexRs);
    }

    public ResponseEntity<CommonRsPersonRs<PersonRs>> login(LoginInfo loginInfo) {
//        Person person = null;
//        validationUtils.validationEmail(loginInfo.getEmail());
//        try {
//            person = personRepository.findByEmail(loginInfo.getEmail()).orElseThrow();
        Person person = personRepository.findByEmail(loginInfo.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email %s is not found", loginInfo.getEmail()))
        );
//        } catch (BadCredentialsException ex) {
//            validationUtils.validationAuthorization(person);
//        }
        validationUtils.validationPassword(accountService.getDecodedPassword(person.getPassword()), loginInfo.getPassword());
//        String token = getToken(person); //todo
        CommonRsPersonRs<PersonRs> commonRsPersonRs = new CommonRsPersonRs<>();
        commonRsPersonRs.setData(getDataPersonRs(person, getToken(person)));
        return ResponseEntity.ok(commonRsPersonRs);
    }

    public ResponseEntity<CaptchaRs> captcha() {
//        GCage gCage = new GCage();
//        String token = gCage.getTokenGenerator().next();
        CaptchaRs captchaRs = new CaptchaRs<>();
//        captchaRs.setCode();
        return ResponseEntity.ok(captchaRs);
    }

    private String getToken(Person person) {
        UserDetails userDetails = userService.loadUserByUsername(person.getEmail());
        return jwtTokenUtils.generateToken(userDetails);
    }

    private Collection<PersonRs> getDataPersonRs(Person person, String token) {
        List<PersonRs> persons = new ArrayList<>();
        PersonRs personRs = new PersonRs();
//        personRs.setEmail(person.getEmail());
        personRs.setToken(token);
        persons.add(personRs);
        return persons;
    }
}

