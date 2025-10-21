package com.example.gateway.common.validation;

import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.common.exception.RequestValidationException;

import java.util.Currency;
import java.util.Locale;

/**
 * Collection of reusable validation helper methods.
 */
public final class ValidationUtils {

    private static final String FIELD_MUST_NOT_BE_BLANK = "%s must not be blank";
    private static final String FIELD_MUST_BE_CURRENCY = "%s must be a valid ISO 4217 currency code";

    private ValidationUtils() {
    }

    /**
     * Ensures that the provided text is not {@code null} or blank. Leading and trailing whitespace
     * are ignored and the trimmed value is returned to the caller.
     *
     * @param value     the value to verify
     * @param fieldName human readable field name used in exception messages
     * @return the trimmed value
     * @throws MissingRequiredValueException if {@code value} is {@code null}
     * @throws RequestValidationException    if {@code value} is blank after trimming
     */
    public static String requireTrimmedNotBlank(String value, String fieldName) {
        if (value == null) {
            throw MissingRequiredValueException.forField(fieldName);
        }
        String candidate = value;
        String trimmed = candidate.trim();
        if (trimmed.isEmpty()) {
            throw new RequestValidationException(FIELD_MUST_NOT_BE_BLANK.formatted(fieldName));
        }
        return trimmed;
    }

    /**
     * Normalises a currency code by trimming, upper-casing and validating it against ISO 4217.
     *
     * @param currencyCode the currency code to validate
     * @param fieldName    human readable field name used in exception messages
     * @return a normalised ISO 4217 currency code
     * @throws MissingRequiredValueException if {@code currencyCode} is {@code null}
     * @throws RequestValidationException    if {@code currencyCode} is blank or not recognised
     */
    public static String normalizeCurrencyCode(String currencyCode, String fieldName) {
        String trimmed = requireTrimmedNotBlank(currencyCode, fieldName);
        String normalized = trimmed.toUpperCase(Locale.ROOT);
        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new RequestValidationException(FIELD_MUST_BE_CURRENCY.formatted(fieldName), ex);
        }
        return normalized;
    }
}
