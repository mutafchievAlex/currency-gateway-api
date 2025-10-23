package com.example.gateway.api.mapper.json;

import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
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

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "instantToOffsetDateTime")
    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }

    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.atOffset(ZoneOffset.UTC);
    }
}
