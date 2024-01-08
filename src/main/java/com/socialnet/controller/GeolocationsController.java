package com.socialnet.controller;

import com.socialnet.dto.response.CommonRs;
import com.socialnet.dto.response.GeolocationRs;
import com.socialnet.service.GeolocationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/geolocations")
public class GeolocationsController {

    private final GeolocationsService geolocationsService;

    @GetMapping("/countries")
    public CommonRs<List<GeolocationRs>> getCountries() {
        return geolocationsService.getCountries();
    }

    @GetMapping("/cities/uses")
    public CommonRs<List<GeolocationRs>> getCitiesUses(@RequestParam(name = "country") String country) {
        return geolocationsService.getCitiesUses(country);
    }

    @GetMapping("/cities/db")
    public CommonRs<List<GeolocationRs>> getCitiesDB(
            @RequestParam(name = "country") String country,
            @RequestParam(name = "starts") String starts) {
        return geolocationsService.getCitiesDB(country, starts);
    }

    @GetMapping("/cities/api")
    public CommonRs<List<GeolocationRs>> getCitiesApi(
            @RequestParam(name = "country") String country,
            @RequestParam(name = "starts") String starts) {
        return geolocationsService.getCitiesApi(country, starts);
    }

}
