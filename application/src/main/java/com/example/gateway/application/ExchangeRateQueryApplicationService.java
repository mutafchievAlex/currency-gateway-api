package com.example.gateway.application;

import com.example.gateway.application.exception.InvalidExchangeRateQueryException;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;

/**
 * Coordinates exchange rate lookups while ensuring requests are recorded and validated.
 */
@Service
public class ExchangeRateQueryApplicationService {

    private static final String HTTP_METHOD_GET = "GET";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final BeanValidationService validationService;
    private final Supplier<Instant> timestampSupplier;

    public ExchangeRateQueryApplicationService(ExchangeRateService exchangeRateService,
                                               RequestLogService requestLogService,
                                               BeanValidationService validationService) {
        this(exchangeRateService, requestLogService, validationService, Instant::now);
    }

    ExchangeRateQueryApplicationService(ExchangeRateService exchangeRateService,
                                        RequestLogService requestLogService,
                                        BeanValidationService validationService,
                                        Supplier<Instant> timestampSupplier) {
        this.exchangeRateService = validationService.requirePresent(exchangeRateService, "exchangeRateService");
        this.requestLogService = validationService.requirePresent(requestLogService, "requestLogService");
        this.validationService = validationService;
        this.timestampSupplier = validationService.requirePresent(timestampSupplier, "timestampSupplier");
    }

    public ExchangeRate getCurrentRate(String requestId,
                                       String endpoint,
                                       String baseCurrency,
                                       String targetCurrency) {
        recordRequest(requestId, endpoint);

        String normalizedBase = ValidationUtils.normalizeCurrencyCode(baseCurrency, "baseCurrency");
        String normalizedTarget = ValidationUtils.normalizeCurrencyCode(targetCurrency, "targetCurrency");

        return exchangeRateService.getLatest(normalizedBase, normalizedTarget);
    }

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
