package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.repository.ExternalRatesClient;
import com.example.gateway.domain.repository.RatesSnapshot;
import com.example.gateway.domain.service.ExchangeRateService;
import com.example.gateway.domain.service.RatesCollectorService;
import com.example.gateway.domain.validation.BeanValidationService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRatesCollectorServiceTest {

    @Mock
    private ExternalRatesClient ratesClient;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private BeanValidationService validationService;

    private RatesCollectorService service;

    @BeforeEach
    void setUp() {
        when(validationService.requirePresent(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new DefaultRatesCollectorService(ratesClient, exchangeRateService, validationService);
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
                .map(ExchangeRate::getTargetCurrency)
                .collect(Collectors.toSet());
        assertEquals(Set.of("EUR", "GBP"), targets);
        assertTrue(captor.getAllValues().stream().allMatch(rate -> rate.getBaseCurrency().equals("USD")));
        assertTrue(captor.getAllValues().stream().allMatch(rate -> rate.getTimestamp().equals(timestamp)));
    }
}
