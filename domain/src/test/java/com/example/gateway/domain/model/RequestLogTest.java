package com.example.gateway.domain.model;

import com.example.gateway.domain.validation.BeanValidationService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestLogTest {

    @Test
    @DisplayName("constructor stores all values")
    void constructorStoresValues() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        RequestLog log = new RequestLog("id-1", "/rates", "GET", timestamp);

        assertEquals("id-1", log.requestId());
        assertEquals("/rates", log.endpoint());
        assertEquals("GET", log.httpMethod());
        assertSame(timestamp, log.timestamp());
    }

    @Test
    @DisplayName("bean validation reports missing request log data")
    void beanValidationDetectsInvalidValues() {
        BeanValidationService validationService =
                new BeanValidationService(Validation.buildDefaultValidatorFactory().getValidator());
        RequestLog log = new RequestLog(null, " ", null, null);

        ConstraintViolationException exception =
                assertThrows(ConstraintViolationException.class, () -> validationService.requireValid(log, "log"));

        assertFalse(exception.getConstraintViolations().isEmpty());
    }
}
