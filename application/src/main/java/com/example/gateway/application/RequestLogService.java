package com.example.gateway.application;

import com.example.gateway.application.port.RequestLogRepositoryPort;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.RequestLog;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RequestLogService {

    private final RequestLogRepositoryPort repository;

    public RequestLogService(RequestLogRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public RequestLog record(RequestLog log) {
        Objects.requireNonNull(log, "log must not be null");
        repository.findByRequestId(log.requestId())
                .ifPresent(existing -> {
                    throw new DuplicateRequestException("Request with id '%s' already logged".formatted(log.requestId()));
                });
        return repository.save(log);
    }
}
