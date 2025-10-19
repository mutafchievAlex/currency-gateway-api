package com.example.gateway.api.json;

import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;

import java.time.Instant;
import java.util.List;

final class JsonApiMapper {

    private JsonApiMapper() {
    }

    static ExchangeRateResponse toResponse(ExchangeRate rate) {
        if (rate == null) {
            return null;
        }
        return new ExchangeRateResponse()
                .baseCurrency(rate.baseCurrency())
                .targetCurrency(rate.targetCurrency())
                .rate(rate.rate())
                .timestamp(rate.timestamp());
    }

    static List<ExchangeRateResponse> toResponseList(List<ExchangeRate> rates) {
        return rates.stream()
                .map(JsonApiMapper::toResponse)
                .toList();
    }

    static RequestLog toRequestLog(String requestId, String endpoint, String httpMethod, Instant timestamp) {
        return new RequestLog(normalize(requestId), endpoint, httpMethod, timestamp);
    }

    static String normalizeCurrency(String currency) {
        if (currency == null) {
            return null;
        }
        return ValidationUtils.normalizeCurrencyCode(currency, "currency");
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
