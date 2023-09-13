package ru.skillbox.socialnet.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComplexRs {
    Long count;
    Integer id;
    String message;
    Long message_id;
}
