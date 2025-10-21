package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.validation.ValidationUtils;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.repository.ExternalRatesClient;
import com.example.gateway.domain.repository.RatesSnapshot;
import com.example.gateway.domain.service.ExchangeRateService;
import com.example.gateway.domain.service.RatesCollectorService;
import com.example.gateway.domain.validation.BeanValidationService;
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
public class DefaultRatesCollectorService implements RatesCollectorService {

    private final ExternalRatesClient externalRatesClient;
    private final ExchangeRateService exchangeRateService;
    private final BeanValidationService validationService;

    public DefaultRatesCollectorService(ExternalRatesClient externalRatesClient,
                                        ExchangeRateService exchangeRateService,
                                        BeanValidationService validationService) {
        this.validationService = validationService;
        this.externalRatesClient = validationService.requirePresent(externalRatesClient, "externalRatesClient");
        this.exchangeRateService = validationService.requirePresent(exchangeRateService, "exchangeRateService");
    }

    @Override
    public void collectLatestRates(String baseCurrency, Collection<String> targetCurrencies) {
        String normalizedBase = ValidationUtils.requireTrimmedNotBlank(baseCurrency, "baseCurrency").toUpperCase(Locale.ROOT);
        Collection<String> safeTargets = validationService.requirePresent(targetCurrencies, "targetCurrencies");

        Set<String> normalizedTargets = safeTargets.stream()
                .filter(Objects::nonNull)
                .map(currency -> currency.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

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
