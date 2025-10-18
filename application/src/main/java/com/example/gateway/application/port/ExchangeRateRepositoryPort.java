package com.example.gateway.application.port;

import com.example.gateway.domain.ExchangeRate;

import java.time.Instant;
import java.util.Optional;

/**
 * Port that exposes persistence capabilities for {@link ExchangeRate} aggregates.
 */
public interface ExchangeRateRepositoryPort {

    Optional<ExchangeRate> findByPairAndTimestamp(String baseCurrency, String targetCurrency, Instant timestamp);

    ExchangeRate save(ExchangeRate rate);
}
