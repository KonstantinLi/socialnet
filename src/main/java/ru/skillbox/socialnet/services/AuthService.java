
package ru.skillbox.socialnet.services;

import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.PersonRs;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.dto.response.CommonRsComplexRs;
import ru.skillbox.socialnet.dto.response.CommonRsPersonRs;
import ru.skillbox.socialnet.dto.response.ComplexRs;
import ru.skillbox.socialnet.entity.Person;
import ru.skillbox.socialnet.entity.other.Captcha;
import ru.skillbox.socialnet.exception.ExceptionBadRq;
import ru.skillbox.socialnet.model.LoginRq;
import ru.skillbox.socialnet.repository.CaptchaRepository;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.security.util.JwtTokenUtils;
import ru.skillbox.socialnet.util.ValidationUtilsRq;
import ru.skillbox.socialnet.util.mapper.PersonMapper;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    public final ValidationUtilsRq validationUtils;
    private final AccountService accountService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;


    public CommonRsComplexRs<ComplexRs> logout(String authorization) {
        CommonRsComplexRs<ComplexRs> commonRsComplexRs = new CommonRsComplexRs<>();
        ComplexRs complexRs = new ComplexRs();
        commonRsComplexRs.setData(complexRs);
        commonRsComplexRs.setTimeStamp(new Date().getTime());
        SecurityContextHolder.clearContext();
        return commonRsComplexRs;
    }

    public CommonRsPersonRs<PersonRs> login(LoginRq loginRq) throws ExceptionBadRq {
        validationUtils.validationEmail(loginRq.getEmail());
        Person person = personRepository.findByEmail(loginRq.getEmail()).orElseThrow(
                () -> new ExceptionBadRq("Пользователь не найден"));
        String password = accountService.getDecodedPassword(person.getPassword());
        validationUtils.validationPassword(password, loginRq.getPassword());
        CommonRsPersonRs<PersonRs> commonRsPersonRs = new CommonRsPersonRs<>();
        PersonRs personRs = PersonMapper.INSTANCE.personToPersonRs(person, "", false);
        personRs.setToken(getToken(person));
        commonRsPersonRs.setData(personRs);
        commonRsPersonRs.setTimeStamp(new Date().getTime());
        return commonRsPersonRs;
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
        captcha.setSecretСode(secretCode);
        Date dateNow = new Date();
//        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
//        captcha.setTime(formatForDateNow.format(dateNow));
        captcha.setTime(LocalDateTime.now().toString());
        return captcha;
    }

    public String getEncodedCaptchaCode(String code) {
        byte[] encodedBytes = Base64.getEncoder().encode(code.getBytes());
        return new String(encodedBytes);
    }

    public String getDecodedCaptchaCode(String code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code);
        return new String(decodedBytes);
    }

    public static int generateRandomInt(int upperRange){
        Random random = new Random();
        return random.nextInt(upperRange);
    }

    private String getToken(Person person) {
        return jwtTokenUtils.generateToken(person);
    }
}

