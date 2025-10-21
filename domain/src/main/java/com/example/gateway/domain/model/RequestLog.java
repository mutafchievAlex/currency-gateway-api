package com.example.gateway.domain.model;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.validation.ValidationUtils;
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
        this.requestId = ValidationUtils.requireTrimmedNotBlank(requestId, "requestId");
        this.endpoint = ValidationUtils.requireTrimmedNotBlank(endpoint, "endpoint");
        this.httpMethod = ValidationUtils.requireTrimmedNotBlank(httpMethod, "httpMethod");
        this.timestamp = requireTimestamp(timestamp);
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

    private static Instant requireTimestamp(Instant timestamp) {
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
        return timestamp;
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
