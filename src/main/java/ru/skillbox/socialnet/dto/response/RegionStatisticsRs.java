package ru.skillbox.socialnet.dto.response;

import lombok.Data;

@Data
public class RegionStatisticsRs {
    private Long countUsers;
    private String region;
}
