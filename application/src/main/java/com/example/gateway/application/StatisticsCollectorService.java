package com.example.gateway.application;

import com.example.gateway.application.port.StatisticsRepositoryPort;
import com.example.gateway.domain.StatisticsEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class StatisticsCollectorService {

    private final StatisticsRepositoryPort repository;

    public StatisticsCollectorService(StatisticsRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public StatisticsEntry record(StatisticsEntry entry) {
        Objects.requireNonNull(entry, "entry must not be null");
        return repository.save(entry);
    }

    public List<StatisticsEntry> retrieve(String metricName, Instant start, Instant end) {
        Objects.requireNonNull(metricName, "metricName must not be null");
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        return repository.findEntriesWithin(metricName, start, end);
    }
}
