package com.example.gateway.domain.model;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.validation.ValidationUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a single metric measurement captured for analytical purposes.
 */
public record StatisticsEntry(@NotBlank String metricName,
                              @NotNull BigDecimal value,
                              @NotNull Instant recordedAt) {

    public StatisticsEntry {
        metricName = ValidationUtils.requireTrimmedNotBlank(metricName, "metricName");
        if (value == null) {
            throw MissingRequiredValueException.forField("value");
        }
        if (recordedAt == null) {
            throw MissingRequiredValueException.forField("recordedAt");
        }
    }
}
