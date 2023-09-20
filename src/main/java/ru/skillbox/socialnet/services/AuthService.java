
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
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.exception.CommonException;
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

    public CommonRsComplexRs<ComplexRs> logout(String authorization) throws CommonException {
        Person person = null;
        Long id = jwtTokenUtils.getId(authorization);
        try {
            person = personRepository.findById(id).get();
        } catch (BadCredentialsException ex) {
            validationUtils.validationAuthorization();
        }
        CommonRsComplexRs<ComplexRs> commonRsComplexRs = new CommonRsComplexRs<>();
        ComplexRs complexRs = new ComplexRs();
        //смапить person в personRs
        commonRsComplexRs.setData(complexRs);
        return commonRsComplexRs;
    }

    public CommonRsPersonRs<PersonRs> login(LoginInfo loginInfo) throws CommonException {
        Person person = null;
        validationUtils.validationEmail(loginInfo.getEmail());
        try {
            person = personRepository.findByEmail(loginInfo.getEmail()).get();
        } catch (BadCredentialsException ex) {
            validationUtils.validationAuthorization();
        }
        validationUtils.validationAuthorization();
        validationUtils.validationPassword(accountService.getDecodedPassword(person.getPassword()), loginInfo.getPassword());
        CommonRsPersonRs<PersonRs> commonRsPersonRs = new CommonRsPersonRs<>();
        PersonRs personRs = new PersonRs();
        //смапить person в personRs
        personRs.setToken(getToken(person));
        commonRsPersonRs.setData(personRs);
        return commonRsPersonRs;
    }

    public CaptchaRs captcha() {
//        GCage gCage = new GCage();
//        String token = gCage.getTokenGenerator().next();
        CaptchaRs captchaRs = new CaptchaRs<>();
//        captchaRs.setCode();
        return captchaRs;
    }

    private String getToken(Person person) {
        UserDetails userDetails = userService.loadUserByUsername(person.getEmail());
        return jwtTokenUtils.generateToken(userDetails);
    }

    private PersonRs getDataPersonRs(Person person, String token) {
        PersonRs personRs = new PersonRs();
//        personRs.setEmail(person.getEmail());
        personRs.setToken(token);
        return personRs;
    }
}

