package com.example.gateway.dataaccess.adapter;

import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.repository.RequestLogRepositoryPort;
import com.example.gateway.dataaccess.entity.RequestLogEntity;
import com.example.gateway.dataaccess.mapper.PersistenceMappers;
import com.example.gateway.dataaccess.repository.RequestLogRepository;
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
