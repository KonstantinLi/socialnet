package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.exception.CommonException;
import ru.skillbox.socialnet.repository.PersonRepository;
import ru.skillbox.socialnet.util.JwtTokenUtils;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

@Service
@RequiredArgsConstructor
public class UserService {
    public final ValidationUtilsRq validationUtils;
    private final JwtTokenUtils jwtTokenUtils;
    private final PersonRepository personRepository;
    public CommonRsPersonRs<PersonRs> userMe(String token) throws CommonException {
        Long id = jwtTokenUtils.getId(token);
        try {
            Person person = personRepository.findById(id).get();
        } catch (BadCredentialsException ex) {
            validationUtils.validationAuthorization();
        }
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        PersonRs personRs = new PersonRs();
        //смапить person в personRs
        personRs.setToken(token);
        return response;
    }
}
