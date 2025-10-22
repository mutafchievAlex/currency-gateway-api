package com.example.gateway.domain.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * Immutable snapshot of exchange rates returned by an external provider.
 */
public record RatesSnapshot(@NotBlank String baseCurrency,
                            @NotNull Instant timestamp,
                            @NotNull Map<String, BigDecimal> rates) {

    public RatesSnapshot {
        baseCurrency = normalize(baseCurrency);
        rates = rates == null ? null : Map.copyOf(rates);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
