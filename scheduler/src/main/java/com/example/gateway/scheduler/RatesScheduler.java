package com.example.gateway.scheduler;

import com.example.gateway.application.RatesCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Periodically triggers the retrieval of exchange rates from external providers.
 */
@Component
public class RatesScheduler {

    private static final Logger log = LoggerFactory.getLogger(RatesScheduler.class);

    private final RatesCollectorService ratesCollectorService;
    private final CollectorJobProperties collectorProperties;
    private final Lock executionLock = new ReentrantLock();

    public RatesScheduler(RatesCollectorService ratesCollectorService, CollectorJobProperties collectorProperties) {
        this.ratesCollectorService = Objects.requireNonNull(ratesCollectorService, "ratesCollectorService must not be null");
        this.collectorProperties = Objects.requireNonNull(collectorProperties, "collectorProperties must not be null");
    }

    @Scheduled(fixedRateString = "${collector.interval}")
    public void collectRates() {
        if (!executionLock.tryLock()) {
            log.debug("Skipping rate collection because a previous execution is still running.");
            return;
        }

        try {
            ratesCollectorService.collectLatestRates(
                    collectorProperties.getBaseCurrency(),
                    collectorProperties.getTargetCurrencies());
        } finally {
            executionLock.unlock();
        }
    }
}
