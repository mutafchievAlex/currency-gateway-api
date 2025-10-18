package com.example.gateway.application.port;

import com.example.gateway.domain.StatisticsEntry;

import java.time.Instant;
import java.util.List;

public interface StatisticsRepositoryPort {

    StatisticsEntry save(StatisticsEntry entry);

    List<StatisticsEntry> findEntriesWithin(String metricName, Instant start, Instant end);
}
