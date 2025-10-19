package com.example.gateway.api.xml;

import com.example.gateway.api.ApiMappingSupport;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.ExchangeRate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface XmlApiMapper extends ApiMappingSupport {

    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }
}
