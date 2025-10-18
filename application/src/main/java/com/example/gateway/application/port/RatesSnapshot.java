package com.example.gateway.application.port;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of exchange rates returned by an external provider.
 */
public record RatesSnapshot(String baseCurrency, Instant timestamp, Map<String, BigDecimal> rates) {

    public RatesSnapshot {
        Objects.requireNonNull(baseCurrency, "baseCurrency must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(rates, "rates must not be null");
        rates = Map.copyOf(rates);
    }
}
