package com.example.gateway.domain.model;

import com.example.gateway.domain.validation.BeanValidationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExchangeRateTest {

    @Test
    @DisplayName("constructor normalizes currency codes and stores values")
    void constructorPopulatesFields() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("1.10"), timestamp);

        assertEquals("USD", rate.getBaseCurrency());
        assertEquals("EUR", rate.getTargetCurrency());
        assertEquals(new BigDecimal("1.10"), rate.getRate());
        assertSame(timestamp, rate.getTimestamp());
    }

    @Test
    @DisplayName("bean validation flags invalid exchange rate values")
    void beanValidationDetectsInvalidValues() {
        BeanValidationService validationService =
                new BeanValidationService(Validation.buildDefaultValidatorFactory().getValidator());
        ExchangeRate rate = new ExchangeRate("", null, BigDecimal.ZERO, null);

        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> validationService.requireValid(rate, "rate"));

        assertFalse(exception.getConstraintViolations().isEmpty());
    }
}
