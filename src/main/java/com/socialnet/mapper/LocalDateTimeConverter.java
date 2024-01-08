package com.socialnet.mapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter
public class LocalDateTimeConverter implements AttributeConverter<Timestamp, LocalDateTime> {
    @Override
    public Timestamp convertToEntityAttribute(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    @Override
    public LocalDateTime convertToDatabaseColumn(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }
}
