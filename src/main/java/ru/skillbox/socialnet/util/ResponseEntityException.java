package ru.skillbox.socialnet.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Getter
public class ResponseEntityException extends Exception {
    private final ResponseEntity<?> responseEntity;
}
