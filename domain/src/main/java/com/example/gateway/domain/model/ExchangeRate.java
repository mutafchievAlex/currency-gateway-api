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
    private String baseCurrency;

    @NotBlank
    private String targetCurrency;

    @NotNull
    @Positive
    private BigDecimal rate;

    @NotNull
    private Instant timestamp;

    public ExchangeRate(String baseCurrency, String targetCurrency, BigDecimal rate, Instant timestamp){
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getRate() {
        return rate;
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
