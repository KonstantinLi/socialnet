package ru.skillbox.socialnet.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.skillbox.socialnet.dto.response.WeatherRs;
import ru.skillbox.socialnet.entity.other.Weather;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        implementationName = "<CLASS_NAME>Imp"
)
public interface WeatherMapper {
    WeatherRs weatherToWeatherRs(Weather weather);
}
