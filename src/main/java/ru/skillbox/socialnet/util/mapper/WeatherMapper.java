package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import ru.skillbox.socialnet.dto.WeatherRs;
import ru.skillbox.socialnet.entity.other.Weather;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherRs toRs(Weather weather);
}
