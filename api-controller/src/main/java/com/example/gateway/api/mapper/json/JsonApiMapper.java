package com.example.gateway.api.mapper.json;

import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.api.mapper.ExchangeRatesApiMapper;
import com.example.gateway.domain.model.ExchangeRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JsonApiMapper extends ExchangeRatesApiMapper<ExchangeRateResponse, ExchangeRateHistoryResponse> {

    @Override
    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    @Override
    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    @Override
    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }
}
