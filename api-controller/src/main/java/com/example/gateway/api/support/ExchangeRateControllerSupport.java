package com.example.gateway.api.support;

import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.application.exception.InvalidExchangeRateQueryException;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.domain.ExchangeRate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Supplier;

public class ExchangeRateControllerSupport<R, H> {

    private static final String HTTP_METHOD_GET = "GET";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final ExchangeRatesApiMapper<R, H> mapper;
    private final Supplier<Instant> timestampSupplier;
    private final BeanValidationService validationService;

    public ExchangeRateControllerSupport(ExchangeRateService exchangeRateService,
                                         RequestLogService requestLogService,
                                         ExchangeRatesApiMapper<R, H> mapper,
                                         BeanValidationService validationService) {
        this(exchangeRateService, requestLogService, mapper, Instant::now, validationService);
    }

    public ExchangeRateControllerSupport(ExchangeRateService exchangeRateService,
                                         RequestLogService requestLogService,
                                         ExchangeRatesApiMapper<R, H> mapper,
                                         Supplier<Instant> timestampSupplier,
                                         BeanValidationService validationService) {
        if (validationService == null) {
            throw MissingRequiredValueException.forField("validationService");
        }
        this.validationService = validationService;
        this.exchangeRateService = validationService.requirePresent(exchangeRateService, "exchangeRateService");
        this.requestLogService = validationService.requirePresent(requestLogService, "requestLogService");
        this.mapper = validationService.requirePresent(mapper, "mapper");
        this.timestampSupplier = validationService.requirePresent(timestampSupplier, "timestampSupplier");
    }

    public R currentRate(String requestId, String endpoint, String baseCurrency, String targetCurrency) {
        recordRequest(requestId, endpoint);

        String normalizedBase = mapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = mapper.normalizeCurrency(targetCurrency);

        ExchangeRate rate = exchangeRateService.getLatest(normalizedBase, normalizedTarget);

        return mapper.toExchangeRateResponse(rate);
    }

    public H history(String requestId,
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

        String normalizedBase = mapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = mapper.normalizeCurrency(targetCurrency);

        Instant startInstant = safeStart.toInstant();
        Instant endInstant = safeEnd.toInstant();

        List<ExchangeRate> history = exchangeRateService.findHistory(normalizedBase, normalizedTarget, startInstant, endInstant);
        return mapper.toHistoryResponse(history);
    }

    private void recordRequest(String requestId, String endpoint) {
        Instant timestamp = timestampSupplier.get();
        requestLogService.record(mapper.toRequestLog(requestId, endpoint, HTTP_METHOD_GET, timestamp));
    }
}
