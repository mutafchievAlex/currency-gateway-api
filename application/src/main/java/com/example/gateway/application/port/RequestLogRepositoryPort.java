package com.example.gateway.application.port;

import com.example.gateway.domain.RequestLog;

import java.util.Optional;

public interface RequestLogRepositoryPort {

    Optional<RequestLog> findByRequestId(String requestId);

    RequestLog save(RequestLog log);
}
