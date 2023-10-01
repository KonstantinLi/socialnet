package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.skillbox.socialnet.dto.response.WeatherRs;
import ru.skillbox.socialnet.entity.locationrelated.Weather;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface WeatherMapper {
    WeatherRs weatherToWeatherRs(Weather weather);
}
