package com.example.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

/**
 * Captures the essential details of an incoming request processed by the gateway.
 */
public final class RequestLog {

    @NotBlank
    private final String requestId;

    @NotBlank
    private final String endpoint;

    @NotBlank
    private final String httpMethod;

    @NotNull
    private final Instant timestamp;

    public RequestLog(String requestId,
                      String endpoint,
                      String httpMethod,
                      Instant timestamp) {
        this.requestId = normalize(requestId);
        this.endpoint = normalize(endpoint);
        this.httpMethod = normalize(httpMethod);
        this.timestamp = timestamp;
    }

    public String requestId() {
        return requestId;
    }

    public String endpoint() {
        return endpoint;
    }

    public String httpMethod() {
        return httpMethod;
    }

    public Instant timestamp() {
        return timestamp;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequestLog that)) {
            return false;
        }
        return Objects.equals(requestId, that.requestId)
                && Objects.equals(endpoint, that.endpoint)
                && Objects.equals(httpMethod, that.httpMethod)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, endpoint, httpMethod, timestamp);
    }

    @Override
    public String toString() {
        return "RequestLog{" +
                "requestId='" + requestId + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
