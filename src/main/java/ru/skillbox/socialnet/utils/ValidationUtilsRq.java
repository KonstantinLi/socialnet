package ru.skillbox.socialnet.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnet.dto.request.exception.CommonException;

@Service
@RequiredArgsConstructor
public class ValidationUtilsRq extends RuntimeException {

    public <T> void validationRegPassword(String password1, String password2) {
        if (password1 == null || password2 == null || !password1.equals(password2)) {
//            throw new CommonException(HttpStatusCode.valueOf(400), "Password is not correct", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("Password is not correct");
        }
    }

    public <T> void validationCode(String code1, String code2) {
        if (!code1.equals(code2)) {
//            throw new CommonException(HttpStatusCode.valueOf(400), "Captcha code is not correct", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("Captcha code is not correct");
        }
    }

    public <T> void validationEmail(String email) {
        if (email.isEmpty()) {
//            throw new CommonException(HttpStatusCode.valueOf(400), "Field 'email' is empty", HttpStatus.BAD_REQUEST);
            throw new RuntimeException("Field 'email' is empty");
        }
    }

    public <T> void validationAuthorization(T req) {
        if (req == null) {
//            throw new CommonException(HttpStatusCode.valueOf(401), "Unauthorized", HttpStatus.UNAUTHORIZED);
            throw new RuntimeException("Unauthorized");
        }
    }

    public <T> void validationPassword(String decodedPassword, String password) {
        if (decodedPassword.equals(password)) {
//            throw new CommonException(HttpStatusCode.valueOf(402), "Forbidden", HttpStatus.FORBIDDEN);
            throw new RuntimeException("Forbidden");
        }
    }

//     if (optionalPost.isEmpty()) {
//        return new ResponseEntity<>(
//                new ErrorRs(ERROR_NO_RECORD_FOUND, "Post record " + id + " not found"),
//                HttpStatusCode.valueOf(400)
//        );
}
