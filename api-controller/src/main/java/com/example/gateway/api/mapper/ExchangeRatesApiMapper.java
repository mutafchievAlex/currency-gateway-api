package com.example.gateway.api.mapper;

import com.example.gateway.domain.model.ExchangeRate;

import java.util.List;

public interface ExchangeRatesApiMapper<R, H> extends MapperSupport {

    R toExchangeRateResponse(ExchangeRate rate);

    List<R> toExchangeRateResponses(List<ExchangeRate> rates);

    H toHistoryResponse(List<ExchangeRate> rates);
}
