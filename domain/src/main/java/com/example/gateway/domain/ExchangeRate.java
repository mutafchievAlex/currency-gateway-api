package com.example.gateway.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the exchange rate between two currencies at a specific instant in time.
 */
public record ExchangeRate(String baseCurrency,
                           String targetCurrency,
                           BigDecimal rate,
                           Instant timestamp) {

    private static final String ISO_CURRENCY_ERROR = "must be a valid ISO 4217 currency code";

    public ExchangeRate {
        baseCurrency = validateCurrency(baseCurrency, "baseCurrency");
        targetCurrency = validateCurrency(targetCurrency, "targetCurrency");
        rate = Objects.requireNonNull(rate, "rate must not be null");
        if (rate.signum() <= 0) {
            throw new IllegalArgumentException("rate must be greater than zero");
        }
        timestamp = Objects.requireNonNull(timestamp, "timestamp must not be null");
    }

    private static String validateCurrency(String currencyCode, String fieldName) {
        Objects.requireNonNull(currencyCode, fieldName + " must not be null");
        String normalized = currencyCode.trim().toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(fieldName + " " + ISO_CURRENCY_ERROR, ex);
        }
        return normalized;
    }
}
