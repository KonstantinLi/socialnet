package ru.skillbox.socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.skillbox.socialnet.dto.response.CommonRs;
import ru.skillbox.socialnet.dto.response.GeolocationRs;
import ru.skillbox.socialnet.dto.response.VKCitiesRs;
import ru.skillbox.socialnet.dto.response.VKCitiesRs.VKCitiesResponse;
import ru.skillbox.socialnet.dto.response.VKCountriesRs;
import ru.skillbox.socialnet.dto.response.VKCountriesRs.VKCountry;
import ru.skillbox.socialnet.entity.locationrelated.Country;
import ru.skillbox.socialnet.repository.CitiesRepository;
import ru.skillbox.socialnet.repository.CountriesRepository;

import java.net.URI;
import java.util.*;


@Service
@RequiredArgsConstructor
public class GeolocationsService {

  @Value("${vk.key}")
  private String vkServiceKey;

  private static final String COUNTRIES_ISO_CODES = "AU, AT, AZ, AX, AL, DZ, UM, VI, AS, AI, AO, AD, AQ, AG, AR, AM, AW, AF, BS, BD, BB, BH, BZ, BY, BE, BJ, BM, BG, BO, BA, BW, BR, IO, VG, BN, BF, BI, VU, VA, GB, HU, VE, TL, VN, GA, HT, GY, GP, GT, GN, GW, DE, GI, HN, HK, GD, GL, GR, GE, GU, DK, CD, DJ, DM, DO, EU, EG, ZM, EH, ZW, IL, IN, ID, JO, IQ, IR, IE, IS, ES, IT, YE, KP, CV, KZ, KY, KH, CM, CA, QA, KE, CY, KG, KI, CN, CC, CO, KM, CR, CI, CU, KW, LA, LV, LS, LR, LB, LY, LT, LI, LU, MU, MR, MG, YT, MO, MK, MW, MY, ML, MV, MT, MA, MQ, MH, MX, MZ, MD, MC, MN, MS, MM, NA, NR, NP, NE, NG, AN, NL, NI, NU, NC, NZ, NO, AE, OM, CX, CK, HM, PK, PW, PS, PA, PG, PY, PE, PN, PL, PT, PR, CG, RE, RU, RW, RO, US, SV, WS, SM, ST, SA, SZ, SJ, MP, SC, SN, VC, KN, LC, PM, RS, CS, SG, SY, SK, SI, SB, SO, SD, SR, SL, SU, TJ, TH, TW, TZ, TG, TK, TO, TT, TV, TN, TM, TR, UG, UZ, UA, UY, FO, FM, FJ, PH, FI, FK, FR, GF, PF, TF, HR, CF, TD, ME, CZ, CL, CH, SE, LK, EC, GQ, ER, EE, ET, ZA, KR, GS, JM, JP, BV, NF, SH, TC, WF";
  private static final String VK_GET_COUNTRIES_URL = "https://api.vk.com/method/database.getCountries";
  private static final String VK_GET_CITIES_URL = "https://api.vk.com/method/database.getCities";
  private static final Double VK_API_VERSION = 5.154;

  private final CountriesRepository countriesRepository;
  private final CitiesRepository citiesRepository;


  public CommonRs<List<GeolocationRs>> getCountries() {

    if (countriesRepository.count() == 0){
      getCountriesFromVkAndSave();
    }

    var geolocationRsList = countriesRepository.findAll()
                                              .stream().map(country ->  GeolocationRs.builder().title(country.getName()).build())
                                              .toList();

    var result = new CommonRs<List<GeolocationRs>>();
    result.setData(geolocationRsList);
    result.setTotal((long) geolocationRsList.size());
    return result;
  }

  public CommonRs<List<GeolocationRs>> getCitiesUses(String country) {
    var cities = citiesRepository.getCitiesByCountryUses(country)
        .stream().map(city ->  GeolocationRs.builder().title(city.getName()).build())
        .toList();

    var result = new CommonRs<List<GeolocationRs>>();
    result.setData(cities);
    result.setTotal((long) cities.size());
    return result;
  }

  public CommonRs<List<GeolocationRs>> getCitiesDB(String country, String starts) {
    var cities = citiesRepository.getCitiesByCountryAndStarts(country, starts)
        .stream().map(city ->  GeolocationRs.builder().title(city.getName()).build())
        .toList();

    var result = new CommonRs<List<GeolocationRs>>();
    result.setData(cities);
    result.setTotal((long) cities.size());
    return result;
  }

  public CommonRs<List<GeolocationRs>> getCitiesApi(String countryName, String starts) {
    var countryOpt =  countriesRepository.findCountryByName(countryName);
    if (countryOpt.isEmpty()) return null;
    Country country = countryOpt.get();

    RestTemplate restTemplate = new RestTemplate();
    var entity = addAuthorization();
    URI uri = buildCityQuery(starts, country.getExternalId(), "ru");
    var response = restTemplate.exchange(uri, HttpMethod.GET, entity, VKCitiesRs.class).getBody();

    var citiesApi =
        Optional.ofNullable(response).map(VKCitiesRs::getResponse).map(VKCitiesResponse::getItems)
                .stream().flatMap(Collection::stream).map(vkCity -> GeolocationRs.builder().title(vkCity.getTitle()).build())
                .toList();

    var result = new CommonRs<List<GeolocationRs>>();
    result.setData(citiesApi);
    result.setTotal((long) citiesApi.size());
    return result;
  }

  public void getCountriesFromVkAndSave() {
    RestTemplate restTemplate = new RestTemplate();
    var entity = addAuthorization();
    URI uri = buildCountryQuery("ru");
    VKCountriesRs responseRu = restTemplate.exchange(uri, HttpMethod.GET, entity, VKCountriesRs.class).getBody();
    URI uriEn= buildCountryQuery("en");
    VKCountriesRs responseEn = restTemplate.exchange(uriEn, HttpMethod.GET, entity, VKCountriesRs.class).getBody();

    if (responseRu == null || responseRu.getResponse() == null) return;
    if (responseEn == null || responseEn.getResponse() == null) return;

    var isoCodes = Arrays.stream(COUNTRIES_ISO_CODES.split(", ")).toList();

    List<Country> countryList = new ArrayList<>();
    for (int i = 0; i < isoCodes.size(); i++){

      VKCountry ruCountry = responseRu.getResponse().getItems().get(i);
      VKCountry enCountry = responseEn.getResponse().getItems().get(i);

      if (ruCountry.getTitle().isEmpty()) continue;

      Country country = new Country();
      country.setExternalId(ruCountry.getId());
      country.setCode2(isoCodes.get(i));
      country.setName(ruCountry.getTitle());
      country.setFullName(ruCountry.getTitle());
      country.setInternationalName(enCountry.getTitle());
      countryList.add(country);
    }

    countriesRepository.saveAll(countryList);
  }


  private HttpEntity<MultiValueMap<String, String>> addAuthorization(){
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + vkServiceKey);
    return new HttpEntity<>(headers);
  }

  private URI buildCountryQuery(String locale){
    return UriComponentsBuilder.fromUriString(VK_GET_COUNTRIES_URL)
        .queryParam("v", VK_API_VERSION)
        .queryParam("need_all", 1)
        .queryParam("offset", 0)
        .queryParam("count", 1000)
        .queryParam("lang", locale)
        .queryParam("code", COUNTRIES_ISO_CODES)
        .build().toUri();
  }

  private URI buildCityQuery(String query, Long countryExtId,  String locale){
    return UriComponentsBuilder.fromUriString(VK_GET_CITIES_URL)
        .queryParam("v", VK_API_VERSION)
        .queryParam("need_all", 1)
        .queryParam("country_id", countryExtId)
        .queryParam("offset", 0)
        .queryParam("count", 1000)
        .queryParam("lang", locale)
        .queryParam("q", query)
        .build().toUri();
  }

}
