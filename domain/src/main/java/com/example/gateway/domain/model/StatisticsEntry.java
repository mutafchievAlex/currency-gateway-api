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
    private String metricName;

    @NotNull
    private BigDecimal value;

    @NotNull
    private Instant timestamp;

    public StatisticsEntry(String metricName,
                           BigDecimal value,
                           Instant timestamp) {
        this.metricName = metricName;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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
