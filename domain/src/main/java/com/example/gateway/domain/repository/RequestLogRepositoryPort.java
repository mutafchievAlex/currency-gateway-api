package com.example.gateway.domain.repository;

import com.example.gateway.domain.model.RequestLog;

import java.util.Optional;
import java.util.UUID;

public interface RequestLogRepositoryPort {

    Optional<RequestLog> findByRequestId(UUID requestId);

    RequestLog save(RequestLog log);
}
