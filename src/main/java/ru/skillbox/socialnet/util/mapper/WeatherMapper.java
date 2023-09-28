package ru.skillbox.socialnet.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.socialnet.dto.response.CaptchaRs;
import ru.skillbox.socialnet.entity.locationrelated.Weather;

@Mapper(componentModel = "spring")
public interface WeatherMapper {
    @SuppressWarnings("unused")
    WeatherMapper INSTANCE = Mappers.getMapper(WeatherMapper.class);

    //TODO что тут происходит? Оо
    CaptchaRs.WeatherRs toRs(Weather weather);
}
