package com.example.gateway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        assertThrows(NullPointerException.class,
                () -> new StatisticsEntry(null, BigDecimal.ONE, recordedAt));
        assertThrows(NullPointerException.class,
                () -> new StatisticsEntry("request.count", null, recordedAt));
        assertThrows(NullPointerException.class,
                () -> new StatisticsEntry("request.count", BigDecimal.ONE, null));
    }
}
