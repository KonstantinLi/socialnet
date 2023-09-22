
package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CaptchaRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.model.LoginRq;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.repository.PersonSettingsRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final ValidationUtilsRq validationUtils;
    private final AccountService accountService;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userService;
    private final PersonRepository personRepository;
    private final PersonSettingsRepository personSettingsRepository;


    public CommonRsComplexRs<ComplexRs> logout(String authorization) throws CommonException {
        CommonRsComplexRs<ComplexRs> commonRsComplexRs = new CommonRsComplexRs<>();
        ComplexRs complexRs = new ComplexRs();
        commonRsComplexRs.setData(complexRs);
        commonRsComplexRs.setTimestamp(new Date().getTime());
        SecurityContextHolder.clearContext();
        return commonRsComplexRs;
    }

    public CommonRsPersonRs<PersonRs> login(LoginRq loginRq) throws CommonException {
        Person person = null;
        validationUtils.validationEmail(loginRq.getEmail());
        try {
            person = personRepository.findByEmail(loginRq.getEmail()).orElseThrow();
        } catch (BadCredentialsException ex) {
            validationUtils.validationUser();
        }
        String password = accountService.getDecodedPassword(person.getPassword());
        validationUtils.validationPassword(password, loginRq.getPassword());
        CommonRsPersonRs<PersonRs> commonRsPersonRs = new CommonRsPersonRs<>();
        PersonRs personRs = PersonMapper.INSTANCE.toRs(person);
        personRs.setToken(getToken(person));
        commonRsPersonRs.setData(personRs);
        commonRsPersonRs.setTimestamp(new Date().getTime());
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
        return jwtTokenUtils.generateToken(person);
    }

    private PersonRs getDataPersonRs(Person person, String token) {
        PersonRs personRs = new PersonRs();
//        personRs.setEmail(person.getEmail());
        personRs.setToken(token);
        return personRs;
    }
}

