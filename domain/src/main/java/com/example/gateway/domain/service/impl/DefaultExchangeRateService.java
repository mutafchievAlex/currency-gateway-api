package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.validation.ValidationUtils;
import com.example.gateway.domain.exception.ExchangeRateNotFoundException;
import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.repository.ExchangeRateRepositoryPort;
import com.example.gateway.domain.service.ExchangeRateService;
import com.example.gateway.domain.validation.BeanValidationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Domain service that encapsulates persistence rules for {@link ExchangeRate} instances.
 */
@Service
public class DefaultExchangeRateService implements ExchangeRateService {

    private final ExchangeRateRepositoryPort repository;
    private final BeanValidationService validationService;

    public DefaultExchangeRateService(ExchangeRateRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @Override
    public boolean saveIfAbsent(ExchangeRate rate) {
        ExchangeRate candidate = validationService.requireValid(rate, "rate");
        return repository.findByPairAndTimestamp(candidate.baseCurrency(), candidate.targetCurrency(), candidate.timestamp())
                .map(existing -> false)
                .orElseGet(() -> {
                    repository.save(candidate);
                    return true;
                });
    }

    @Override
    public ExchangeRate getLatest(String baseCurrency, String targetCurrency) {
        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");
        return repository.findLatestByPair(normalizedBase, normalizedTarget)
                .orElseThrow(() -> new ExchangeRateNotFoundException(normalizedBase, normalizedTarget));
    }

    @Override
    public List<ExchangeRate> findHistory(String baseCurrency,
                                          String targetCurrency,
                                          Instant start,
                                          Instant end) {
        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");
        Instant safeStart = validationService.requirePresent(start, "start");
        Instant safeEnd = validationService.requirePresent(end, "end");
        if (safeStart.isAfter(safeEnd)) {
            throw new InvalidExchangeRateQueryException("start must be before or equal to end");
        }
        return repository.findWithinRange(normalizedBase, normalizedTarget, safeStart, safeEnd);
    }
}
