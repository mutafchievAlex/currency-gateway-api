package com.example.gateway.dataaccess.adapter;

import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.domain.repository.StatisticsRepositoryPort;
import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import com.example.gateway.dataaccess.mapper.PersistenceMappers;
import com.example.gateway.dataaccess.repository.StatisticsRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JpaStatisticsRepositoryAdapter implements StatisticsRepositoryPort {

    private final StatisticsRepository repository;
    private final PersistenceMappers mapper;

    public JpaStatisticsRepositoryAdapter(StatisticsRepository repository, PersistenceMappers mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public StatisticsEntry save(StatisticsEntry entry) {
        StatisticsEntryEntity saved = repository.save(mapper.toEntity(entry));
        return mapper.toDomain(saved);
    }

    @Override
    public List<StatisticsEntry> findEntriesWithin(String metricName, Instant start, Instant end) {
        return repository.findByMetricNameAndTimestampBetweenOrderByTimestampAsc(metricName, start, end)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
