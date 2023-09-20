package ru.skillbox.socialnet.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.skillbox.socialnet.dto.response.ErrorRs;

@RequiredArgsConstructor
@Getter
public class UnauthorizedException extends RuntimeException {
}
