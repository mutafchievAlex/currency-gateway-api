package com.example.gateway.api;

import com.example.gateway.api.generated.api.ExchangeRatesApi;
import com.example.gateway.api.generated.model.ApiErrorRepresentation;
import com.example.gateway.api.generated.model.ExchangeRateHistoryRepresentation;
import com.example.gateway.api.generated.model.ExchangeRateRepresentation;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchange-rates")
@Validated
public class ExchangeRateController implements ExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final ApiMapper apiMapper;

    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  RequestLogService requestLogService,
                                  ApiMapper apiMapper) {
        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService must not be null");
        this.requestLogService = Objects.requireNonNull(requestLogService, "requestLogService must not be null");
        this.apiMapper = Objects.requireNonNull(apiMapper, "apiMapper must not be null");
    }

    @Override
    @GetMapping(value = "/current", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ExchangeRateRepresentation> getCurrentExchangeRate(String requestId,
                                                                             String baseCurrency,
                                                                             String targetCurrency) {
        ExchangeRateRepresentation response = getCurrentRate(requestId, baseCurrency, targetCurrency);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(value = "/history", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ExchangeRateHistoryRepresentation> getExchangeRateHistory(String requestId,
                                                                                    String baseCurrency,
                                                                                    String targetCurrency,
                                                                                    Instant start,
                                                                                    Instant end) {
        ExchangeRateHistoryRepresentation history = getHistory(requestId, baseCurrency, targetCurrency, start, end);
        return ResponseEntity.ok(history);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ApiErrorRepresentation> handleDuplicate(DuplicateRequestException exception,
                                                                  NativeWebRequest request) {
        return errorResponse(HttpStatus.CONFLICT, "Duplicate request", exception.getMessage(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorRepresentation> handleStatus(ResponseStatusException exception,
                                                               NativeWebRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String title = Optional.ofNullable(exception.getReason()).orElse(status.getReasonPhrase());
        return errorResponse(status, title, exception.getReason(), request);
    }

    private ExchangeRateRepresentation getCurrentRate(String requestId, String baseCurrency, String targetCurrency) {
        recordRequest(requestId, CURRENT_ENDPOINT);

        String normalizedBase = apiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = apiMapper.normalizeCurrency(targetCurrency);

        ExchangeRate rate = exchangeRateService.findLatest(normalizedBase, normalizedTarget)
                .orElseThrow(() -> notFound(normalizedBase, normalizedTarget));

        return apiMapper.toRepresentation(rate);
    }

    private ExchangeRateHistoryRepresentation getHistory(String requestId,
                                                         String baseCurrency,
                                                         String targetCurrency,
                                                         Instant start,
                                                         Instant end) {
        recordRequest(requestId, HISTORY_ENDPOINT);

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before or equal to end");
        }

        String normalizedBase = apiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = apiMapper.normalizeCurrency(targetCurrency);

        List<ExchangeRate> history = exchangeRateService.findHistory(normalizedBase, normalizedTarget, start, end);
        return apiMapper.toHistoryRepresentation(history);
    }

    private void recordRequest(String requestId, String endpoint) {
        requestLogService.record(apiMapper.toRequestLog(requestId, endpoint, "GET", Instant.now()));
    }

    private ResponseStatusException notFound(String baseCurrency, String targetCurrency) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No exchange rate found for %s/%s".formatted(baseCurrency, targetCurrency));
    }

    private boolean acceptsXml(NativeWebRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .map(MediaType::parseMediaTypes)
                .orElse(List.of(MediaType.ALL))
                .stream()
                .anyMatch(mediaType -> mediaType.isCompatibleWith(MediaType.APPLICATION_XML));
    }

    private ResponseEntity<ApiErrorRepresentation> errorResponse(HttpStatus status,
                                                                 String title,
                                                                 String detail,
                                                                 NativeWebRequest request) {
        String safeDetail = Optional.ofNullable(detail).orElse(title);
        ApiErrorRepresentation error = new ApiErrorRepresentation()
                .title(title)
                .detail(safeDetail)
                .status(status.value());

        MediaType contentType = acceptsXml(request) ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;
        return ResponseEntity.status(status)
                .contentType(contentType)
                .body(error);
    }
}
