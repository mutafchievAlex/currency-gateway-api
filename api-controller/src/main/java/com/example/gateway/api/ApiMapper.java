package com.example.gateway.api;

import com.example.gateway.api.generated.model.ExchangeRateHistoryRepresentation;
import com.example.gateway.api.generated.model.ExchangeRateRepresentation;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ApiMapper {

    ExchangeRateRepresentation toRepresentation(ExchangeRate rate);

    List<ExchangeRateRepresentation> toRepresentationList(List<ExchangeRate> rates);

    default ExchangeRateHistoryRepresentation toHistoryRepresentation(List<ExchangeRate> rates) {
        ExchangeRateHistoryRepresentation representation = new ExchangeRateHistoryRepresentation();
        representation.setRates(toRepresentationList(rates));
        return representation;
    }

    default RequestLog toRequestLog(String requestId, String endpoint, String httpMethod, Instant timestamp) {
        Objects.requireNonNull(requestId, "requestId must not be null");
        Objects.requireNonNull(endpoint, "endpoint must not be null");
        Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        return new RequestLog(requestId.trim(), endpoint, httpMethod, timestamp);
    }

    default String normalizeCurrency(String currency) {
        if (currency == null) {
            return null;
        }
        return ValidationUtils.normalizeCurrencyCode(currency, "currency");
    }
}
