package com.example.gateway.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Locale;

/**
 * Validator implementation for {@link CurrencyCode}.
 */
public final class CurrencyCodeValidator implements ConstraintValidator<CurrencyCode, CharSequence> {

    private boolean allowNull;

    @Override
    public void initialize(CurrencyCode constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }

        String normalized = value.toString().trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return false;
        }

        try {
            Currency.getInstance(normalized);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
