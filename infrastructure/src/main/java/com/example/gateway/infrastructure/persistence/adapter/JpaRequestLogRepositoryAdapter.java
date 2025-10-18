package com.example.gateway.infrastructure.persistence.adapter;

import com.example.gateway.application.port.RequestLogRepositoryPort;
import com.example.gateway.domain.RequestLog;
import com.example.gateway.infrastructure.persistence.entity.RequestLogEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import com.example.gateway.infrastructure.persistence.repository.RequestLogRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaRequestLogRepositoryAdapter implements RequestLogRepositoryPort {

    private final RequestLogRepository repository;

    public JpaRequestLogRepositoryAdapter(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RequestLog> findByRequestId(String requestId) {
        return repository.findByRequestId(requestId)
                .map(PersistenceMappers::toDomain);
    }

    @Override
    public RequestLog save(RequestLog log) {
        RequestLogEntity saved = repository.save(PersistenceMappers.toEntity(log));
        return PersistenceMappers.toDomain(saved);
    }
}
