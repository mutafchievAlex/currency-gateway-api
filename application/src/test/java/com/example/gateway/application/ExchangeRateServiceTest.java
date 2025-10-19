package com.example.gateway.application;

import com.example.gateway.application.port.ExchangeRateRepositoryPort;
import com.example.gateway.domain.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepositoryPort repository;

    @InjectMocks
    private ExchangeRateService service;

    private ExchangeRate rate;
    private Instant timestamp;

    @BeforeEach
    void setUp() {
        timestamp = Instant.parse("2024-01-01T00:00:00Z");
        rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.9100"), timestamp);
    }

    @Test
    void savesRateWhenEntryIsMissing() {
        when(repository.findByPairAndTimestamp(rate.baseCurrency(), rate.targetCurrency(), rate.timestamp()))
                .thenReturn(Optional.empty());
        when(repository.save(rate)).thenReturn(rate);

        boolean persisted = service.saveIfAbsent(rate);

        assertTrue(persisted);
        verify(repository).save(rate);
    }

    @Test
    void skipsPersistenceWhenDuplicateExists() {
        when(repository.findByPairAndTimestamp(rate.baseCurrency(), rate.targetCurrency(), rate.timestamp()))
                .thenReturn(Optional.of(rate));

        boolean persisted = service.saveIfAbsent(rate);

        assertFalse(persisted);
        verify(repository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void findsLatestRate() {
        when(repository.findLatestByPair("USD", "EUR")).thenReturn(Optional.of(rate));

        Optional<ExchangeRate> latest = service.findLatest("usd", "eur");

        assertTrue(latest.isPresent());
        assertTrue(latest.map(ExchangeRate::timestamp).filter(timestamp::equals).isPresent());
    }

    @Test
    void findsHistoryWithinRange() {
        Instant start = timestamp.minus(1, ChronoUnit.DAYS);
        Instant end = timestamp.plus(1, ChronoUnit.DAYS);
        when(repository.findWithinRange("USD", "EUR", start, end)).thenReturn(List.of(rate));

        List<ExchangeRate> history = service.findHistory("usd", "eur", start, end);

        assertFalse(history.isEmpty());
        assertTrue(history.stream().anyMatch(entry -> entry.timestamp().equals(timestamp)));
    }

    @Test
    void findLatestRejectsInvalidCurrencyCode() {
        assertThrows(IllegalArgumentException.class, () -> service.findLatest("US1", "eur"));
        verify(repository, never()).findLatestByPair(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void findHistoryRejectsInvalidCurrencyCode() {
        Instant start = timestamp.minus(1, ChronoUnit.DAYS);
        Instant end = timestamp.plus(1, ChronoUnit.DAYS);

        assertThrows(IllegalArgumentException.class, () -> service.findHistory("usd", "EU", start, end));
        verify(repository, never()).findWithinRange(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
