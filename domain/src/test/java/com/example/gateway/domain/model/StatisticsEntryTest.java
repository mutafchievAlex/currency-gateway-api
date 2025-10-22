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

class StatisticsEntryTest {

    @Test
    @DisplayName("constructor stores metric details")
    void constructorStoresValues() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        StatisticsEntry entry = new StatisticsEntry("request.count", new BigDecimal("10"), timestamp);

        assertEquals("request.count", entry.metricName());
        assertEquals(new BigDecimal("10"), entry.value());
        assertSame(timestamp, entry.timestamp());
    }

    @Test
    @DisplayName("bean validation reports missing statistic details")
    void beanValidationDetectsInvalidValues() {
        BeanValidationService validationService =
                new BeanValidationService(Validation.buildDefaultValidatorFactory().getValidator());
        StatisticsEntry entry = new StatisticsEntry(" ", null, null);

        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> validationService.requireValid(entry, "entry"));

        assertFalse(exception.getConstraintViolations().isEmpty());
    }
}
