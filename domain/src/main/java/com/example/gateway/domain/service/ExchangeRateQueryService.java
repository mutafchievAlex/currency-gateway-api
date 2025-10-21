package com.example.gateway.domain.service;

import com.example.gateway.domain.model.ExchangeRate;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service boundary for querying exchange rates while applying validation and request logging.
 */
public interface ExchangeRateQueryService {

    ExchangeRate getCurrentRate(String requestId,
                                String endpoint,
                                String baseCurrency,
                                String targetCurrency);

    List<ExchangeRate> getHistory(String requestId,
                                  String endpoint,
                                  String baseCurrency,
                                  String targetCurrency,
                                  OffsetDateTime start,
                                  OffsetDateTime end);
}
