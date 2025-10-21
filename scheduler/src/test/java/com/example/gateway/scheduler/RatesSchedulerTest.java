package com.example.gateway.scheduler;

import com.example.gateway.domain.service.RatesCollectorService;
import com.example.gateway.domain.validation.BeanValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RatesSchedulerTest {

    private RatesCollectorService ratesCollectorService;
    private CollectorJobProperties collectorJobProperties;
    private RatesScheduler scheduler;
    private BeanValidationService validationService;

    @BeforeEach
    void setUp() {
        ratesCollectorService = mock(RatesCollectorService.class);
        collectorJobProperties = new CollectorJobProperties();
        collectorJobProperties.setBaseCurrency("USD");
        collectorJobProperties.setTargetCurrencies(List.of("EUR", "GBP"));
        validationService = mock(BeanValidationService.class);
        doAnswer(invocation -> invocation.getArgument(0)).when(validationService).requirePresent(any(), anyString());
        scheduler = new RatesScheduler(ratesCollectorService, collectorJobProperties, validationService);
    }

    @Test
    void delegatesToCollectorServiceWithConfiguredCurrencies() {
        scheduler.collectRates();

        ArgumentCaptor<String> baseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<String>> targetsCaptor = ArgumentCaptor.forClass(List.class);

        verify(ratesCollectorService).collectLatestRates(baseCaptor.capture(), targetsCaptor.capture());

        assertEquals("USD", baseCaptor.getValue());
        assertEquals(collectorJobProperties.getTargetCurrencies(), targetsCaptor.getValue());
    }

    @Test
    void preventsConcurrentExecutions() throws Exception {
        CountDownLatch invocationStarted = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        doAnswer(invocation -> {
            invocationStarted.countDown();
            release.await(1, TimeUnit.SECONDS);
            return null;
        }).when(ratesCollectorService).collectLatestRates(anyString(), anyCollection());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(() -> scheduler.collectRates());

            assertTrue(invocationStarted.await(1, TimeUnit.SECONDS));

            scheduler.collectRates();

            verify(ratesCollectorService, times(1)).collectLatestRates("USD", collectorJobProperties.getTargetCurrencies());

            release.countDown();
            future.get(1, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }
}
