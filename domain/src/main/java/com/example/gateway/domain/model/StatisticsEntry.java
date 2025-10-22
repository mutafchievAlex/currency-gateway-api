package com.example.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a single metric measurement captured for analytical purposes.
 */
public final class StatisticsEntry {

    @NotBlank
    private final String metricName;

    @NotNull
    private final BigDecimal value;

    @NotNull
    private final Instant timestamp;

    public StatisticsEntry(String metricName,
                           BigDecimal value,
                           Instant timestamp) {
        this.metricName = normalize(metricName);
        this.value = value;
        this.timestamp = timestamp;
    }

    public String metricName() {
        return metricName;
    }

    public BigDecimal value() {
        return value;
    }

    public Instant timestamp() {
        return timestamp;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StatisticsEntry that)) {
            return false;
        }
        return Objects.equals(metricName, that.metricName)
                && Objects.equals(value, that.value)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, value, timestamp);
    }

    @Override
    public String toString() {
        return "StatisticsEntry{" +
                "metricName='" + metricName + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
