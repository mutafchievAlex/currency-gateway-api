package com.example.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the exchange rate between two currencies at a specific instant in time.
 */
public final class ExchangeRate {

    @NotBlank
    private final String baseCurrency;

    @NotBlank
    private final String targetCurrency;

    @NotNull
    @Positive
    private final BigDecimal rate;

    @NotNull
    private final Instant timestamp;

    public ExchangeRate(String baseCurrency,
                        String targetCurrency,
                        BigDecimal rate,
                        Instant timestamp) {
        this.baseCurrency = normalizeCurrency(baseCurrency);
        this.targetCurrency = normalizeCurrency(targetCurrency);
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public String baseCurrency() {
        return baseCurrency;
    }

    public String targetCurrency() {
        return targetCurrency;
    }

    public BigDecimal rate() {
        return rate;
    }

    public Instant timestamp() {
        return timestamp;
    }

    private static String normalizeCurrency(String currencyCode) {
        if (currencyCode == null) {
            return null;
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExchangeRate that)) {
            return false;
        }
        return Objects.equals(baseCurrency, that.baseCurrency)
                && Objects.equals(targetCurrency, that.targetCurrency)
                && Objects.equals(rate, that.rate)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, rate, timestamp);
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", rate=" + rate +
                ", timestamp=" + timestamp +
                '}';
    }
}
