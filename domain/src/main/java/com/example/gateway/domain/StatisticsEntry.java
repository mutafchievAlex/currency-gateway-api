package com.example.gateway.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single metric measurement captured for analytical purposes.
 */
public record StatisticsEntry(String metricName,
                              BigDecimal value,
                              Instant recordedAt) {

    public StatisticsEntry {
        metricName = Objects.requireNonNull(metricName, "metricName must not be null");
        value = Objects.requireNonNull(value, "value must not be null");
        recordedAt = Objects.requireNonNull(recordedAt, "recordedAt must not be null");
    }
}
