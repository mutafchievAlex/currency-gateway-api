package com.example.gateway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExchangeRateTest {

    @Test
    @DisplayName("constructor normalizes currency codes and stores values")
    void constructorPopulatesFields() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        ExchangeRate rate = new ExchangeRate("usd", "eur", new BigDecimal("1.10"), timestamp);

        assertEquals("USD", rate.baseCurrency());
        assertEquals("EUR", rate.targetCurrency());
        assertEquals(new BigDecimal("1.10"), rate.rate());
        assertSame(timestamp, rate.timestamp());
    }

    @Test
    @DisplayName("constructor rejects invalid ISO currency codes")
    void constructorRejectsInvalidCurrencies() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");

        assertThrows(IllegalArgumentException.class,
                () -> new ExchangeRate("US1", "EUR", BigDecimal.ONE, timestamp));

        assertThrows(IllegalArgumentException.class,
                () -> new ExchangeRate("USD", "EU", BigDecimal.ONE, timestamp));
    }

    @Test
    @DisplayName("constructor rejects null or non-positive values")
    void constructorRejectsInvalidValues() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");

        assertThrows(NullPointerException.class,
                () -> new ExchangeRate(null, "EUR", BigDecimal.ONE, timestamp));

        assertThrows(NullPointerException.class,
                () -> new ExchangeRate("USD", null, BigDecimal.ONE, timestamp));

        assertThrows(NullPointerException.class,
                () -> new ExchangeRate("USD", "EUR", null, timestamp));

        assertThrows(IllegalArgumentException.class,
                () -> new ExchangeRate("USD", "EUR", BigDecimal.ZERO, timestamp));

        assertThrows(IllegalArgumentException.class,
                () -> new ExchangeRate("USD", "EUR", BigDecimal.ONE.negate(), timestamp));

        assertThrows(NullPointerException.class,
                () -> new ExchangeRate("USD", "EUR", BigDecimal.ONE, null));
    }
}
