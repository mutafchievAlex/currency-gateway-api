package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.validation.ValidationUtils;
import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import com.example.gateway.domain.service.ExchangeRateService;
import com.example.gateway.domain.service.RequestLogService;
import com.example.gateway.domain.validation.BeanValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;

/**
 * Coordinates exchange rate lookups while ensuring requests are recorded and validated.
 */
@Service
public class DefaultExchangeRateQueryService implements ExchangeRateQueryService {

    private static final String HTTP_METHOD_GET = "GET";

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
    public ExchangeRate getCurrentRate(String requestId,
                                       String endpoint,
                                       String baseCurrency,
                                       String targetCurrency) {
        recordRequest(requestId, endpoint);

        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");

        return exchangeRateService.getLatest(normalizedBase, normalizedTarget);
    }

    @Override
    public List<ExchangeRate> getHistory(String requestId,
                                         String endpoint,
                                         String baseCurrency,
                                         String targetCurrency,
                                         OffsetDateTime start,
                                         OffsetDateTime end) {
        recordRequest(requestId, endpoint);

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

    private void recordRequest(String requestId, String endpoint) {
        Instant timestamp = timestampSupplier.get();
        RequestLog log = new RequestLog(requestId, endpoint, HTTP_METHOD_GET, timestamp);
        requestLogService.record(log);
    }
}
