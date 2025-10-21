package com.example.gateway.api.support;

import com.example.gateway.domain.ExchangeRate;

import java.util.List;

public interface ExchangeRatesApiMapper<R, H> extends ApiMappingSupport {

    R toExchangeRateResponse(ExchangeRate rate);

    List<R> toExchangeRateResponses(List<ExchangeRate> rates);

    H toHistoryResponse(List<ExchangeRate> rates);
}
