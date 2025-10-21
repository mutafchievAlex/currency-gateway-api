package com.example.gateway.api.mapper.xml;

import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.api.mapper.OffsetDateTimeMapper;
import com.example.gateway.domain.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = OffsetDateTimeMapper.class)
public interface XmlApiMapper {

    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "instantToOffsetDateTime")
    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }
}
