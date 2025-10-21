package com.example.gateway.domain.validation;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Centralises bean validation so services can assert their inputs are present and satisfy the
 * constraints declared on domain types without repeating the same boilerplate checks.
 */
@Component
public class BeanValidationService {

    private final Validator validator;

    public BeanValidationService(Validator validator) {
        this.validator = validator;
    }

    public <T> T requireValid(T value, String fieldName) {
        T candidate = requirePresent(value, fieldName);
        Set<ConstraintViolation<T>> violations = validator.validate(candidate);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return candidate;
    }

    public <T> T requirePresent(T value, String fieldName) {
        if (value == null) {
            throw MissingRequiredValueException.forField(fieldName);
        }
        return value;
    }
}
