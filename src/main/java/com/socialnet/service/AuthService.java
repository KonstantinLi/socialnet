package com.socialnet.service;

import com.github.cage.GCage;
import com.socialnet.dto.request.LoginRq;
import com.socialnet.dto.response.CaptchaRs;
import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.ComplexRs;
import com.socialnet.dto.response.PersonRs;
import com.socialnet.entity.other.Captcha;
import com.socialnet.repository.CaptchaRepository;
import com.socialnet.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.socialnet.annotation.Debug;
import com.socialnet.entity.personrelated.Person;
import com.socialnet.exception.AuthException;
import com.socialnet.mapper.PersonMapper;
import com.socialnet.security.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Debug
public class AuthService {
    private final AccountService accountService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final PersonMapper personMapper;
    private final Random random = new Random();

    public CommonRs<ComplexRs> logout() {
        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        commonRs.setData(complexRs);
        SecurityContextHolder.clearContext();
        return commonRs;
    }

    public CommonRs<PersonRs> login(LoginRq loginRq) {
        Person person = personRepository.findByEmail(loginRq.getEmail()).orElseThrow(
                () -> AuthException.userNotFoundByEmail(loginRq.getEmail()));
        String password = accountService.getDecodedPassword(person.getPassword());
        if (!loginRq.getPassword().equals(password)) {
            throw AuthException.incorrectPassword();
        } else if (person.getIsBlocked() != null && person.getIsBlocked()) {
            throw AuthException.userIsBlocked();
        }
        CommonRs<PersonRs> commonRs = new CommonRs<>();
        PersonRs personRs = personMapper.personToPersonRs(person,
                "", false);
        personRs.setToken(getToken(person));
        commonRs.setData(personRs);

        return commonRs;
    }

    public CaptchaRs captcha() {

        GCage gCage = new GCage();
        String code = Integer.toString(generateRandomInt(100000));
        String secretCode = getEncodedCaptchaCode(code);
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(gCage.draw(code));
        Captcha captcha = addCaptcha(code, secretCode);
        captchaRepository.save(captcha);
        CaptchaRs captchaRs = new CaptchaRs();
        captchaRs.setCode(secretCode);
        captchaRs.setImage(image);

        return captchaRs;
    }

    private Captcha addCaptcha(String code, String secretCode) {

        Captcha captcha = new Captcha();
        captcha.setCode(code);
        captcha.setSecretCode(secretCode);
        captcha.setTime(LocalDateTime.now().toString());

        return captcha;
    }

    public String getEncodedCaptchaCode(String code) {
        byte[] encodedBytes = Base64.getEncoder().encode(code.getBytes());
        return new String(encodedBytes);
    }

    public int generateRandomInt(int upperRange) {
        return random.nextInt(upperRange);
    }

    private String getToken(Person person) {
        return jwtTokenUtils.generateToken(person);
    }
}

