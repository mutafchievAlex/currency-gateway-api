package com.example.gateway.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "request_logs",
        indexes = {
                @Index(name = "idx_request_logs_timestamp", columnList = "timestamp")
        })
public class RequestLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

    @NotBlank
    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @NotBlank
    @Column(name = "http_method", nullable = false)
    private String httpMethod;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    protected RequestLogEntity() {
        // JPA
    }

    public RequestLogEntity(String requestId, String endpoint, String httpMethod, Instant timestamp) {
        this.requestId = requestId;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
