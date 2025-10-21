package com.example.gateway.api.support;

import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.RequestLog;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface ApiMappingSupport {

    default RequestLog toRequestLog(String requestId, String endpoint, String httpMethod, Instant timestamp) {
        String safeRequestId = ValidationUtils.requireTrimmedNotBlank(requestId, "requestId");
        String safeEndpoint = ValidationUtils.requireTrimmedNotBlank(endpoint, "endpoint");
        String safeMethod = ValidationUtils.requireTrimmedNotBlank(httpMethod, "httpMethod");
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
        Instant safeTimestamp = timestamp;
        return new RequestLog(safeRequestId, safeEndpoint, safeMethod, safeTimestamp);
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

    default String safeDetail(String detail, String fallback) {
        return detail != null ? detail : fallback;
    }
}
