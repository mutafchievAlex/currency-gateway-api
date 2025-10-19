package com.example.gateway.api.json;

import com.example.gateway.api.ApiMappingSupport;
import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.ExchangeRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JsonApiMapper extends ApiMappingSupport {

    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }
}
