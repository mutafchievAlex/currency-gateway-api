package com.example.gateway.api;

import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.RequestLog;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

public interface ApiMappingSupport {

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

    default OffsetDateTime toOffsetDateTime(Instant timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.atOffset(ZoneOffset.UTC);
    }

}
