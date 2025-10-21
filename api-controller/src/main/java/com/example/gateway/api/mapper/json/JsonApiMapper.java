package com.example.gateway.api.mapper.json;

import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.api.mapper.OffsetDateTimeMapper;
import com.example.gateway.domain.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = OffsetDateTimeMapper.class)
public interface JsonApiMapper {

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "instantToOffsetDateTime")
    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }
}
