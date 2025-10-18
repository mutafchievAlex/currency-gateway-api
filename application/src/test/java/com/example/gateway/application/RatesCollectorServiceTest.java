package com.example.gateway.application;

import com.example.gateway.application.port.ExternalRatesClient;
import com.example.gateway.application.port.RatesSnapshot;
import com.example.gateway.domain.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatesCollectorServiceTest {

    @Mock
    private ExternalRatesClient ratesClient;

    @Mock
    private ExchangeRateService exchangeRateService;

    private RatesCollectorService service;

    @BeforeEach
    void setUp() {
        service = new RatesCollectorService(ratesClient, exchangeRateService);
    }

    @Test
    void mapsSnapshotRatesIntoDomainObjects() {
        Instant timestamp = Instant.parse("2024-02-01T10:15:30Z");
        Map<String, BigDecimal> rates = Map.of(
                "EUR", new BigDecimal("0.9200"),
                "GBP", new BigDecimal("0.7800"),
                "JPY", new BigDecimal("145.1000")
        );

        when(ratesClient.fetchLatestRates("USD", Set.of("EUR", "GBP")))
                .thenReturn(new RatesSnapshot("USD", timestamp, rates));

        service.collectLatestRates("USD", Set.of("EUR", "GBP"));

        ArgumentCaptor<ExchangeRate> captor = ArgumentCaptor.forClass(ExchangeRate.class);
        verify(exchangeRateService, times(2)).saveIfAbsent(captor.capture());

        Set<String> targets = captor.getAllValues().stream()
                .map(ExchangeRate::targetCurrency)
                .collect(Collectors.toSet());
        assertEquals(Set.of("EUR", "GBP"), targets);
        assertTrue(captor.getAllValues().stream().allMatch(rate -> rate.baseCurrency().equals("USD")));
        assertTrue(captor.getAllValues().stream().allMatch(rate -> rate.timestamp().equals(timestamp)));
    }
}
