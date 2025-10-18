package com.example.gateway.infrastructure.persistence.adapter;

import com.example.gateway.application.port.StatisticsRepositoryPort;
import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.infrastructure.persistence.entity.StatisticsEntryEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import com.example.gateway.infrastructure.persistence.repository.StatisticsRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JpaStatisticsRepositoryAdapter implements StatisticsRepositoryPort {

    private final StatisticsRepository repository;

    public JpaStatisticsRepositoryAdapter(StatisticsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StatisticsEntry save(StatisticsEntry entry) {
        StatisticsEntryEntity saved = repository.save(PersistenceMappers.toEntity(entry));
        return PersistenceMappers.toDomain(saved);
    }

    @Override
    public List<StatisticsEntry> findEntriesWithin(String metricName, Instant start, Instant end) {
        return repository.findByMetricNameAndRecordedAtBetweenOrderByRecordedAtAsc(metricName, start, end)
                .stream()
                .map(PersistenceMappers::toDomain)
                .collect(Collectors.toList());
    }
}
