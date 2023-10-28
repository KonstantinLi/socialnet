package ru.skillbox.socialnet.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VKCountriesRs {

  private VKCountriesResponse response;

  @Getter
  @Setter
  public static class VKCountriesResponse{
    private Integer count;
    private List<VKCountry> items;
  }

  @Getter
  @Setter
  public static class VKCountry{
    private Long id;
    private String title;
  }

}
