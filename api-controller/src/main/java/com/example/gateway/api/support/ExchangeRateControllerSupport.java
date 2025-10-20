package com.example.gateway.api.support;

import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class ExchangeRateControllerSupport<R, H, E> {

    private static final String HTTP_METHOD_GET = "GET";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final ExchangeRatesApiMapper<R, H, E> mapper;
    private final Supplier<Instant> timestampSupplier;

    public ExchangeRateControllerSupport(ExchangeRateService exchangeRateService,
                                         RequestLogService requestLogService,
                                         ExchangeRatesApiMapper<R, H, E> mapper) {
        this(exchangeRateService, requestLogService, mapper, Instant::now);
    }

    public ExchangeRateControllerSupport(ExchangeRateService exchangeRateService,
                                         RequestLogService requestLogService,
                                         ExchangeRatesApiMapper<R, H, E> mapper,
                                         Supplier<Instant> timestampSupplier) {
        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService must not be null");
        this.requestLogService = Objects.requireNonNull(requestLogService, "requestLogService must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
        this.timestampSupplier = Objects.requireNonNull(timestampSupplier, "timestampSupplier must not be null");
    }

    public R currentRate(String requestId, String endpoint, String baseCurrency, String targetCurrency) {
        recordRequest(requestId, endpoint);

        String normalizedBase = mapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = mapper.normalizeCurrency(targetCurrency);

        ExchangeRate rate = exchangeRateService.findLatest(normalizedBase, normalizedTarget)
                .orElseThrow(() -> notFound(normalizedBase, normalizedTarget));

        return mapper.toExchangeRateResponse(rate);
    }

    public H history(String requestId,
                     String endpoint,
                     String baseCurrency,
                     String targetCurrency,
                     OffsetDateTime start,
                     OffsetDateTime end) {
        recordRequest(requestId, endpoint);

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before or equal to end");
        }

        String normalizedBase = mapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = mapper.normalizeCurrency(targetCurrency);

        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();

        List<ExchangeRate> history = exchangeRateService.findHistory(normalizedBase, normalizedTarget, startInstant, endInstant);
        return mapper.toHistoryResponse(history);
    }

    public E duplicateError(DuplicateRequestException exception) {
        Objects.requireNonNull(exception, "exception must not be null");
        return mapper.createError(HttpStatus.CONFLICT, "Duplicate request", exception.getMessage());
    }

    public E statusError(ResponseStatusException exception) {
        Objects.requireNonNull(exception, "exception must not be null");
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String title = Optional.ofNullable(exception.getReason()).orElse(status.getReasonPhrase());
        return mapper.createError(status, title, exception.getReason());
    }

    private void recordRequest(String requestId, String endpoint) {
        Instant timestamp = timestampSupplier.get();
        requestLogService.record(mapper.toRequestLog(requestId, endpoint, HTTP_METHOD_GET, timestamp));
    }

    private ResponseStatusException notFound(String baseCurrency, String targetCurrency) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No exchange rate found for %s/%s".formatted(baseCurrency, targetCurrency));
    }
}
