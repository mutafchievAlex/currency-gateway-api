package com.example.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Captures the essential details of an incoming request processed by the gateway.
 */
public final class RequestLog {

    @NotNull
    private UUID requestId;

    @NotBlank
    private String endpoint;

    @NotBlank
    private String httpMethod;

    @NotNull
    private Instant timestamp;

    public RequestLog(UUID requestId,
                      String endpoint,
                      String httpMethod,
                      Instant timestamp) {
        this.requestId = requestId;
        this.endpoint = normalize(endpoint);
        this.httpMethod = normalize(httpMethod);
        this.timestamp = timestamp;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
