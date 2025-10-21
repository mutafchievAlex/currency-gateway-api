package com.example.gateway.domain.model;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticsEntryTest {

    @Test
    @DisplayName("constructor stores metric details")
    void constructorStoresValues() {
        Instant recordedAt = Instant.parse("2024-01-01T00:00:00Z");
        StatisticsEntry entry = new StatisticsEntry("request.count", new BigDecimal("10"), recordedAt);

        assertEquals("request.count", entry.metricName());
        assertEquals(new BigDecimal("10"), entry.value());
        assertSame(recordedAt, entry.recordedAt());
    }

    @Test
    @DisplayName("constructor rejects null arguments")
    void constructorRejectsNulls() {
        Instant recordedAt = Instant.parse("2024-01-01T00:00:00Z");

        assertThrows(MissingRequiredValueException.class,
                () -> new StatisticsEntry(null, BigDecimal.ONE, recordedAt));
        assertThrows(MissingRequiredValueException.class,
                () -> new StatisticsEntry("request.count", null, recordedAt));
        assertThrows(MissingRequiredValueException.class,
                () -> new StatisticsEntry("request.count", BigDecimal.ONE, null));
    }
}
