package com.example.gateway.domain.repository;

import com.example.gateway.domain.model.RequestLog;

import java.util.Optional;

public interface RequestLogRepositoryPort {

    Optional<RequestLog> findByRequestId(String requestId);

    RequestLog save(RequestLog log);
}
