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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @BeforeEach
    void setUp() {
        rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.9100"), Instant.parse("2024-01-01T00:00:00Z"));
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
}
