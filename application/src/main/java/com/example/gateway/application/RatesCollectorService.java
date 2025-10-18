package com.example.gateway.application;

import com.example.gateway.application.port.ExternalRatesClient;
import com.example.gateway.application.port.RatesSnapshot;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Coordinates the retrieval of exchange rate information from an external provider
 * and persists the resulting domain objects.
 */
@Service
public class RatesCollectorService {

    private final ExternalRatesClient externalRatesClient;
    private final ExchangeRateService exchangeRateService;

    public RatesCollectorService(ExternalRatesClient externalRatesClient, ExchangeRateService exchangeRateService) {
        this.externalRatesClient = Objects.requireNonNull(externalRatesClient, "externalRatesClient must not be null");
        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService must not be null");
    }

    public void collectLatestRates(String baseCurrency, Collection<String> targetCurrencies) {
        Objects.requireNonNull(baseCurrency, "baseCurrency must not be null");
        Objects.requireNonNull(targetCurrencies, "targetCurrencies must not be null");

        Set<String> normalizedTargets = targetCurrencies.stream()
                .filter(Objects::nonNull)
                .map(currency -> currency.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        String normalizedBase = baseCurrency.trim().toUpperCase(Locale.ROOT);
        RatesSnapshot snapshot = externalRatesClient.fetchLatestRates(normalizedBase, normalizedTargets);

        snapshot.rates().forEach((targetCurrency, rateValue) -> {
            String normalizedTarget = targetCurrency == null ? null : targetCurrency.trim().toUpperCase(Locale.ROOT);
            if (normalizedTarget == null || normalizedTarget.isEmpty()) {
                return;
            }
            if (!normalizedTargets.isEmpty() && !normalizedTargets.contains(normalizedTarget)) {
                return;
            }
            ExchangeRate rate = new ExchangeRate(snapshot.baseCurrency(), normalizedTarget, rateValue, snapshot.timestamp());
            exchangeRateService.saveIfAbsent(rate);
        });
    }
}
