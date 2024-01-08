package com.socialnet.mapper;

import com.socialnet.dto.response.WeatherRs;
import com.socialnet.entity.locationrelated.Weather;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface WeatherMapper {
    WeatherRs weatherToWeatherRs(Weather weather);
}
