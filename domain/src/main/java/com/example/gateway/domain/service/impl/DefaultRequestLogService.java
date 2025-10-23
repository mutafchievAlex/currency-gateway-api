package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.exception.DuplicateRequestException;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.repository.RequestLogRepositoryPort;
import com.example.gateway.domain.service.RequestLogService;
import com.example.gateway.domain.validation.BeanValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultRequestLogService implements RequestLogService {

    private final RequestLogRepositoryPort repository;
    private final BeanValidationService validationService;

    @Autowired
    public DefaultRequestLogService(RequestLogRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @Override
    public RequestLog record(RequestLog log) {
        RequestLog candidate = validationService.requireValid(log, "log");
        repository.findByRequestId(candidate.getRequestId())
                .ifPresent(existing -> {
                    throw new DuplicateRequestException("Request with id '%s' already logged".formatted(candidate.getRequestId()));
                });
        return repository.save(candidate);
    }
}
