package ru.skillbox.socialnet.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VKCitiesRs {

  private VKCitiesResponse response;

  @Getter
  @Setter
  public static class VKCitiesResponse{
    private Integer count;
    private List<VKCity> items;
  }

  @Getter
  @Setter
  public static class VKCity{
    private Long id;
    private String title;
    private String area;
    private String region;
  }

}
