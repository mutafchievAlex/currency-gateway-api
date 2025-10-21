package com.example.gateway.common.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyCodeValidatorTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void shouldAcceptValidCurrencyCode() {
        record Sample(@CurrencyCode String currency) { }

        Set<?> violations = validator.validate(new Sample(" usd "));
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldRejectInvalidCurrencyCode() {
        record Sample(@CurrencyCode String currency) { }

        Set<?> violations = validator.validate(new Sample("ABCDEF"));
        assertEquals(1, violations.size());
    }

    @Test
    void shouldRespectAllowNullFlag() {
        record Sample(@CurrencyCode(allowNull = true) String currency) { }

        Set<?> violations = validator.validate(new Sample(null));
        assertTrue(violations.isEmpty());
    }
}
