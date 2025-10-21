package com.example.gateway.domain;

import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.common.validation.ValidationUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Captures the essential details of an incoming request processed by the gateway.
 */
public record RequestLog(@NotBlank String requestId,
                         @NotBlank String endpoint,
                         @NotBlank String httpMethod,
                         @NotNull Instant timestamp) {

    public RequestLog {
        requestId = ValidationUtils.requireTrimmedNotBlank(requestId, "requestId");
        endpoint = ValidationUtils.requireTrimmedNotBlank(endpoint, "endpoint");
        httpMethod = ValidationUtils.requireTrimmedNotBlank(httpMethod, "httpMethod");
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
    }
}
