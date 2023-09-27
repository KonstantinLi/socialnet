package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.entity.other.Weather;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    WeatherMapper INSTANCE = Mappers.getMapper(WeatherMapper.class);

    CaptchaRs.WeatherRs toRs(Weather weather);
}
