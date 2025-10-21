package com.example.gateway.domain.model;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.validation.ValidationUtils;
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
    private final Instant recordedAt;

    public StatisticsEntry(String metricName,
                           BigDecimal value,
                           Instant recordedAt) {
        this.metricName = ValidationUtils.requireTrimmedNotBlank(metricName, "metricName");
        if (value == null) {
            throw MissingRequiredValueException.forField("value");
        }
        if (recordedAt == null) {
            throw MissingRequiredValueException.forField("recordedAt");
        }
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public String metricName() {
        return metricName;
    }

    public BigDecimal value() {
        return value;
    }

    public Instant recordedAt() {
        return recordedAt;
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
                && Objects.equals(recordedAt, that.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, value, recordedAt);
    }

    @Override
    public String toString() {
        return "StatisticsEntry{" +
                "metricName='" + metricName + '\'' +
                ", value=" + value +
                ", recordedAt=" + recordedAt +
                '}';
    }
}
