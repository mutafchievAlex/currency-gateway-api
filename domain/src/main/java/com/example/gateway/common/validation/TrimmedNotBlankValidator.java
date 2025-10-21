package com.example.gateway.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for {@link TrimmedNotBlank}.
 */
public final class TrimmedNotBlankValidator implements ConstraintValidator<TrimmedNotBlank, CharSequence> {

    private boolean allowNull;

    @Override
    public void initialize(TrimmedNotBlank constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }
        return value.toString().trim().length() > 0;
    }
}
