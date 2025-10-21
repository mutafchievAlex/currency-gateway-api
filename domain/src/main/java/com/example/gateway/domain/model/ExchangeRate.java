package com.example.gateway.domain.model;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.exception.RequestValidationException;
import com.example.gateway.domain.validation.ValidationUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the exchange rate between two currencies at a specific instant in time.
 */
public final class ExchangeRate {

    private static final String ISO_CURRENCY_ERROR = "must be a valid ISO 4217 currency code";

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
        this.baseCurrency = validateCurrency(baseCurrency, "baseCurrency");
        this.targetCurrency = validateCurrency(targetCurrency, "targetCurrency");
        this.rate = validateRate(rate);
        this.timestamp = requireTimestamp(timestamp);
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

    private static String validateCurrency(String currencyCode, String fieldName) {
        String normalized = ValidationUtils.requireTrimmedNotBlank(currencyCode, fieldName).toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RequestValidationException(fieldName + " " + ISO_CURRENCY_ERROR, ex);
        }
        return normalized;
    }

    private static BigDecimal validateRate(BigDecimal value) {
        if (value == null) {
            throw MissingRequiredValueException.forField("rate");
        }
        if (value.signum() <= 0) {
            throw new RequestValidationException("rate must be greater than zero");
        }
        return value;
    }

    private static Instant requireTimestamp(Instant timestamp) {
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
        return timestamp;
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
