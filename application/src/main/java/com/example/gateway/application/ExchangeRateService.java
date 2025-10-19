package com.example.gateway.application;

import com.example.gateway.application.port.ExchangeRateRepositoryPort;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service that encapsulates persistence rules for {@link ExchangeRate} instances.
 */
@Service
public class ExchangeRateService {

    private final ExchangeRateRepositoryPort repository;

    public ExchangeRateService(ExchangeRateRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public boolean saveIfAbsent(ExchangeRate rate) {
        Objects.requireNonNull(rate, "rate must not be null");
        return repository.findByPairAndTimestamp(rate.baseCurrency(), rate.targetCurrency(), rate.timestamp())
                .map(existing -> false)
                .orElseGet(() -> {
                    repository.save(rate);
                    return true;
                });
    }

    public Optional<ExchangeRate> findLatest(String baseCurrency, String targetCurrency) {
        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");
        return repository.findLatestByPair(normalizedBase, normalizedTarget);
    }

    public List<ExchangeRate> findHistory(String baseCurrency,
                                          String targetCurrency,
                                          Instant start,
                                          Instant end) {
        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must not be after end");
        }
        return repository.findWithinRange(normalizedBase, normalizedTarget, start, end);
    }
}
