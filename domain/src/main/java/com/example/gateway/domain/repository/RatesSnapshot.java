package com.example.gateway.domain.repository;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.validation.ValidationUtils;
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
        baseCurrency = ValidationUtils.requireTrimmedNotBlank(baseCurrency, "baseCurrency");
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
        if (rates == null) {
            throw MissingRequiredValueException.forField("rates");
        }
        rates = Map.copyOf(rates);
    }
}
