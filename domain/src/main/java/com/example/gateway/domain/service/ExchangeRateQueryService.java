package com.example.gateway.domain.service;

import com.example.gateway.domain.model.ExchangeRate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service boundary for querying exchange rates while applying validation and request logging.
 */
public interface ExchangeRateQueryService {

    ExchangeRate getCurrentRate(UUID requestId,
                                String endpoint,
                                String baseCurrency,
                                String targetCurrency);

    ExchangeRate getCurrentRate(UUID requestId,
                                String endpoint,
                                Instant requestTimestamp,
                                String clientId,
                                String baseCurrency,
                                String targetCurrency);

    List<ExchangeRate> getHistory(UUID requestId,
                                  String endpoint,
                                  String baseCurrency,
                                  String targetCurrency,
                                  OffsetDateTime start,
                                  OffsetDateTime end);

    List<ExchangeRate> getHistory(UUID requestId,
                                  String endpoint,
                                  Instant requestTimestamp,
                                  String clientId,
                                  String baseCurrency,
                                  String targetCurrency,
                                  OffsetDateTime start,
                                  OffsetDateTime end);
}
