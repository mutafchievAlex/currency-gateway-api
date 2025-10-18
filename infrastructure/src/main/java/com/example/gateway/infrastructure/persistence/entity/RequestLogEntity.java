package com.example.gateway.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "request_logs",
        indexes = {
                @Index(name = "idx_request_logs_timestamp", columnList = "logged_at"),
                @Index(name = "idx_request_logs_endpoint", columnList = "endpoint")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_request_logs_request_id", columnNames = "request_id")
        })
public class RequestLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", nullable = false, length = 100)
    private String requestId;

    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    protected RequestLogEntity() {
        // JPA
    }

    public RequestLogEntity(String requestId, String endpoint, String httpMethod, Instant loggedAt) {
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint");
        this.httpMethod = Objects.requireNonNull(httpMethod, "httpMethod");
        this.loggedAt = Objects.requireNonNull(loggedAt, "loggedAt");
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

    public Instant getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(Instant loggedAt) {
        this.loggedAt = loggedAt;
    }
}
