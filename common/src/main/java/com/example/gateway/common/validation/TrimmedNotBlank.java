package com.example.gateway.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a {@link CharSequence} is not blank after being trimmed.
 * <p>
 * This annotation is useful for incoming data where leading or trailing whitespace should
 * be ignored during validation.
 * </p>
 */
@Target({FIELD, METHOD, PARAMETER, RECORD_COMPONENT, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TrimmedNotBlankValidator.class)
public @interface TrimmedNotBlank {

    String message() default "must not be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Allows {@code null} values to pass validation. When {@code false} (the default),
     * {@code null} values are reported as violations.
     */
    boolean allowNull() default false;
}
