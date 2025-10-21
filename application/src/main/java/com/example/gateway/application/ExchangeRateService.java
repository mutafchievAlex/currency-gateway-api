package com.example.gateway.application;

import com.example.gateway.application.exception.ExchangeRateNotFoundException;
import com.example.gateway.application.exception.InvalidExchangeRateQueryException;
import com.example.gateway.application.port.ExchangeRateRepositoryPort;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Application service that encapsulates persistence rules for {@link ExchangeRate} instances.
 */
@Service
public class ExchangeRateService {

    private final ExchangeRateRepositoryPort repository;
    private final BeanValidationService validationService;

    public ExchangeRateService(ExchangeRateRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public boolean saveIfAbsent(ExchangeRate rate) {
        ExchangeRate candidate = validationService.requireValid(rate, "rate");
        return repository.findByPairAndTimestamp(candidate.baseCurrency(), candidate.targetCurrency(), candidate.timestamp())
                .map(existing -> false)
                .orElseGet(() -> {
                    repository.save(candidate);
                    return true;
                });
    }

    public ExchangeRate getLatest(String baseCurrency, String targetCurrency) {
        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");
        return repository.findLatestByPair(normalizedBase, normalizedTarget)
                .orElseThrow(() -> new ExchangeRateNotFoundException(normalizedBase, normalizedTarget));
    }

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
