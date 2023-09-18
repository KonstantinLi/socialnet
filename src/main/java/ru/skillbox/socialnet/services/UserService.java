package ru.skillbox.socialnet.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.data.entity.Person;
import ru.skillbox.socialnet.dto.request.PersonRs;
import ru.skillbox.socialnet.dto.request.response.CommonRsPersonRs;
import ru.skillbox.socialnet.utils.ValidationUtilsRq;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    public final ValidationUtilsRq validationUtils;
    public ResponseEntity<CommonRsPersonRs<PersonRs>> userMe() {
        Person person = null;
        try {
//            person = personRsRepository.findByСhangePasswordToken(authorization);
        } catch (BadCredentialsException ex) {
            validationUtils.validationAuthorization(person);
        }
        CommonRsPersonRs<PersonRs> response = new CommonRsPersonRs<>();
        List<PersonRs> data = new ArrayList<>();
        data.add(new PersonRs());
        response.setData(data);
        return ResponseEntity.ok(response);
    }
}
