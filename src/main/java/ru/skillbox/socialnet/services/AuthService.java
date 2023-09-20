
package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CaptchaRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.request.response.ComplexRs;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.model.LoginRq;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

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
            person = personRepository.findById(id).orElseThrow();
        } catch (BadCredentialsException ex) {
            validationUtils.validationUser();
        }
        CommonRsComplexRs<ComplexRs> commonRsComplexRs = new CommonRsComplexRs<>();
        ComplexRs complexRs = new ComplexRs();
        commonRsComplexRs.setData(complexRs);
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

