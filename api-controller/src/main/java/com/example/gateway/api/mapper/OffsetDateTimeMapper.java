package com.example.gateway.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface OffsetDateTimeMapper {

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.atOffset(ZoneOffset.UTC);
    }
}
