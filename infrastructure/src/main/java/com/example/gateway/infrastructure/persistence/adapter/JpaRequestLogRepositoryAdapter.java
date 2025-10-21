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
    private final PersistenceMappers mapper;

    public JpaRequestLogRepositoryAdapter(RequestLogRepository repository, PersistenceMappers mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<RequestLog> findByRequestId(String requestId) {
        return repository.findByRequestId(requestId)
                .map(mapper::toDomain);
    }

    @Override
    public RequestLog save(RequestLog log) {
        RequestLogEntity saved = repository.save(mapper.toEntity(log));
        return mapper.toDomain(saved);
    }
}
