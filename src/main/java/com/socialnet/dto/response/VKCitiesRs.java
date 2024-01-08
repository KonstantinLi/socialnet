package com.socialnet.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VKCitiesRs {

    private VKCitiesResponse response;

    @Getter
    @Setter
    public static class VKCitiesResponse {
        private Integer count;
        private List<VKCity> items;
    }

    @Getter
    @Setter
    public static class VKCity {
        private Long id;
        private String title;
        private String area;
        private String region;
    }

}
