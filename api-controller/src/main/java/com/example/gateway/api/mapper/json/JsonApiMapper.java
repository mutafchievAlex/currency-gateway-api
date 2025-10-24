package com.example.gateway.api.mapper.json;

import com.example.gateway.api.json.generated.model.JsonQuote;
import com.example.gateway.domain.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface JsonApiMapper {

    @Mapping(target = "provider", constant = "Fixer")
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "instantToOffsetDateTime")
    JsonQuote toQuote(ExchangeRate rate);

    List<JsonQuote> toQuotes(List<ExchangeRate> rates);

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.atOffset(ZoneOffset.UTC);
    }
}
