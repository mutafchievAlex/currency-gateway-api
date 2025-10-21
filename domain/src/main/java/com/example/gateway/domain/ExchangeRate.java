package com.example.gateway.domain;

import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.common.exception.RequestValidationException;
import com.example.gateway.common.validation.ValidationUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;

/**
 * Represents the exchange rate between two currencies at a specific instant in time.
 */
public record ExchangeRate(@NotBlank String baseCurrency,
                           @NotBlank String targetCurrency,
                           @NotNull @Positive BigDecimal rate,
                           @NotNull Instant timestamp) {

    private static final String ISO_CURRENCY_ERROR = "must be a valid ISO 4217 currency code";

    public ExchangeRate {
        baseCurrency = validateCurrency(baseCurrency, "baseCurrency");
        targetCurrency = validateCurrency(targetCurrency, "targetCurrency");
        rate = validateRate(rate);
        if (timestamp == null) {
            throw MissingRequiredValueException.forField("timestamp");
        }
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

    private static BigDecimal validateRate(BigDecimal rate) {
        if (rate == null) {
            throw MissingRequiredValueException.forField("rate");
        }
        BigDecimal value = rate;
        if (value.signum() <= 0) {
            throw new RequestValidationException("rate must be greater than zero");
        }
        return value;
    }
}
