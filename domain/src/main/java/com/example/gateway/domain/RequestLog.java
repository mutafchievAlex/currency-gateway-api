package com.example.gateway.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Captures the essential details of an incoming request processed by the gateway.
 */
public record RequestLog(String requestId,
                         String endpoint,
                         String httpMethod,
                         Instant timestamp) {

    public RequestLog {
        requestId = Objects.requireNonNull(requestId, "requestId must not be null");
        endpoint = Objects.requireNonNull(endpoint, "endpoint must not be null");
        httpMethod = Objects.requireNonNull(httpMethod, "httpMethod must not be null");
        timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
    }
}
