package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import com.example.gateway.domain.service.ExchangeRateService;
import com.example.gateway.domain.service.RequestLogService;
import com.example.gateway.domain.validation.BeanValidationService;
import com.example.gateway.domain.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Coordinates exchange rate lookups while ensuring requests are recorded and validated.
 */
@Service
public class DefaultExchangeRateQueryService implements ExchangeRateQueryService {

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final BeanValidationService validationService;
    private final Supplier<Instant> timestampSupplier;

    @Autowired
    public DefaultExchangeRateQueryService(ExchangeRateService exchangeRateService,
                                           RequestLogService requestLogService,
                                           BeanValidationService validationService) {
        this.exchangeRateService = exchangeRateService;
        this.requestLogService = requestLogService;
        this.validationService = validationService;
        this.timestampSupplier = Instant::now;
    }

    @Override
    public ExchangeRate getCurrentRate(UUID requestId,
                                       String endpoint,
                                       String baseCurrency,
                                       String targetCurrency) {
        Instant timestamp = timestampSupplier.get();
        return getCurrentRateInternal(requestId, endpoint, HTTP_METHOD_GET, timestamp, baseCurrency, targetCurrency);
    }

    @Override
    public ExchangeRate getCurrentRate(UUID requestId,
                                       String endpoint,
                                       Instant requestTimestamp,
                                       String clientId,
                                       String baseCurrency,
                                       String targetCurrency) {
        Instant safeTimestamp = validationService.requirePresent(requestTimestamp, "timestamp");
        ValidationUtils.requireTrimmedNotBlank(clientId, "client.id");
        return getCurrentRateInternal(requestId, endpoint, HTTP_METHOD_POST, safeTimestamp, baseCurrency, targetCurrency);
    }

    @Override
    public List<ExchangeRate> getHistory(UUID requestId,
                                         String endpoint,
                                         String baseCurrency,
                                         String targetCurrency,
                                         OffsetDateTime start,
                                         OffsetDateTime end) {
        Instant timestamp = timestampSupplier.get();
        return getHistoryInternal(requestId, endpoint, HTTP_METHOD_GET, timestamp, baseCurrency, targetCurrency, start, end);
    }

    @Override
    public List<ExchangeRate> getHistory(UUID requestId,
                                         String endpoint,
                                         Instant requestTimestamp,
                                         String clientId,
                                         String baseCurrency,
                                         String targetCurrency,
                                         OffsetDateTime start,
                                         OffsetDateTime end) {
        Instant safeTimestamp = validationService.requirePresent(requestTimestamp, "timestamp");
        ValidationUtils.requireTrimmedNotBlank(clientId, "client.id");
        return getHistoryInternal(requestId, endpoint, HTTP_METHOD_POST, safeTimestamp, baseCurrency, targetCurrency, start, end);
    }

    private ExchangeRate getCurrentRateInternal(UUID requestId,
                                                String endpoint,
                                                String httpMethod,
                                                Instant timestamp,
                                                String baseCurrency,
                                                String targetCurrency) {
        recordRequest(requestId, endpoint, httpMethod, timestamp);

        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");

        return exchangeRateService.getLatest(normalizedBase, normalizedTarget);
    }

    private List<ExchangeRate> getHistoryInternal(UUID requestId,
                                                  String endpoint,
                                                  String httpMethod,
                                                  Instant timestamp,
                                                  String baseCurrency,
                                                  String targetCurrency,
                                                  OffsetDateTime start,
                                                  OffsetDateTime end) {
        recordRequest(requestId, endpoint, httpMethod, timestamp);

        OffsetDateTime safeStart = validationService.requirePresent(start, "start");
        OffsetDateTime safeEnd = validationService.requirePresent(end, "end");

        if (safeStart.isAfter(safeEnd)) {
            throw new InvalidExchangeRateQueryException("start must be before or equal to end");
        }

        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");

        Instant startInstant = safeStart.toInstant();
        Instant endInstant = safeEnd.toInstant();

        return exchangeRateService.findHistory(normalizedBase, normalizedTarget, startInstant, endInstant);
    }

    private void recordRequest(UUID requestId,
                               String endpoint,
                               String httpMethod,
                               Instant timestamp) {
        UUID safeRequestId = validationService.requirePresent(requestId, "requestId");
        Instant safeTimestamp = validationService.requirePresent(timestamp, "timestamp");
        RequestLog log = new RequestLog(safeRequestId, endpoint, httpMethod, safeTimestamp);
        requestLogService.record(log);
    }
}
