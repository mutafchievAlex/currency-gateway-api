package com.example.gateway.application;

import com.example.gateway.application.port.RequestLogRepositoryPort;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.RequestLog;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {

    private final RequestLogRepositoryPort repository;
    private final BeanValidationService validationService;

    public RequestLogService(RequestLogRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public RequestLog record(RequestLog log) {
        RequestLog candidate = validationService.requireValid(log, "log");
        repository.findByRequestId(candidate.requestId())
                .ifPresent(existing -> {
                    throw new DuplicateRequestException("Request with id '%s' already logged".formatted(candidate.requestId()));
                });
        return repository.save(candidate);
    }
}
