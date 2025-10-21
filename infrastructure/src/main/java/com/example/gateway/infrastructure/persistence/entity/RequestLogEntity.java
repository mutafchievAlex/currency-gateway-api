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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
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

}
