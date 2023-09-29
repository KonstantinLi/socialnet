package ru.skillbox.socialnet.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ComplexRs {

    private Long count;
    private Integer id;
    private String message;
    private Long messageId;
}
