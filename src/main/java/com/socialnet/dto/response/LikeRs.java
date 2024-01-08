package com.socialnet.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class LikeRs {
    private Integer likes;
    private Set<Long> users;
}
