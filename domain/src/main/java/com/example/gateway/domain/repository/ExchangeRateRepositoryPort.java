package com.example.gateway.domain.repository;

import com.example.gateway.domain.model.ExchangeRate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Port that exposes persistence capabilities for {@link ExchangeRate} aggregates.
 */
public interface ExchangeRateRepositoryPort {

    Optional<ExchangeRate> findByPairAndTimestamp(String baseCurrency, String targetCurrency, Instant timestamp);

    ExchangeRate save(ExchangeRate rate);

    Optional<ExchangeRate> findLatestByPair(String baseCurrency, String targetCurrency);

    List<ExchangeRate> findWithinRange(String baseCurrency, String targetCurrency, Instant start, Instant end);
}
