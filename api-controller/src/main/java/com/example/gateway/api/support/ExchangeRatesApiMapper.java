package com.example.gateway.api.support;

import com.example.gateway.domain.ExchangeRate;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface ExchangeRatesApiMapper<R, H, E> extends ApiMappingSupport {

    R toExchangeRateResponse(ExchangeRate rate);

    List<R> toExchangeRateResponses(List<ExchangeRate> rates);

    H toHistoryResponse(List<ExchangeRate> rates);

    E createError(HttpStatus status, String title, String detail);
}
