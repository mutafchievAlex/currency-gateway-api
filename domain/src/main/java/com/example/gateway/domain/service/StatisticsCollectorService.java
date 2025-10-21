package com.example.gateway.domain.service;

import com.example.gateway.domain.model.StatisticsEntry;

import java.time.Instant;
import java.util.List;

/**
 * Service boundary for collecting and querying statistics.
 */
public interface StatisticsCollectorService {

    StatisticsEntry record(StatisticsEntry entry);

    List<StatisticsEntry> retrieve(String metricName, Instant start, Instant end);
}
