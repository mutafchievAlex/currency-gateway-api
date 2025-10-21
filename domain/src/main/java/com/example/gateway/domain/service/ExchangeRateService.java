package com.example.gateway.domain.service;

import com.example.gateway.domain.model.ExchangeRate;

import java.time.Instant;
import java.util.List;

/**
 * Service boundary for coordinating {@link ExchangeRate} persistence and retrieval operations.
 */
public interface ExchangeRateService {

    boolean saveIfAbsent(ExchangeRate rate);

    ExchangeRate getLatest(String baseCurrency, String targetCurrency);

    List<ExchangeRate> findHistory(String baseCurrency,
                                   String targetCurrency,
                                   Instant start,
                                   Instant end);
}
