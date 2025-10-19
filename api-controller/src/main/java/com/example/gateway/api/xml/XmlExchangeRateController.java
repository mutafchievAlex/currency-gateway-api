package com.example.gateway.api.xml;

import com.example.gateway.api.xml.generated.api.XmlExchangeRatesApi;
import com.example.gateway.api.xml.generated.model.ApiError;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/exchange-rates", produces = MediaType.APPLICATION_XML_VALUE)
@Validated
public class XmlExchangeRateController implements XmlExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;
    private final XmlApiMapper xmlApiMapper;

    public XmlExchangeRateController(ExchangeRateService exchangeRateService,
                                     RequestLogService requestLogService,
                                     XmlApiMapper xmlApiMapper) {
        this.exchangeRateService = Objects.requireNonNull(exchangeRateService, "exchangeRateService must not be null");
        this.requestLogService = Objects.requireNonNull(requestLogService, "requestLogService must not be null");
        this.xmlApiMapper = Objects.requireNonNull(xmlApiMapper, "xmlApiMapper must not be null");
    }

    @Override
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ExchangeRateResponse> getCurrentExchangeRate(String requestId,
                                                                       String baseCurrency,
                                                                       String targetCurrency) {
        ExchangeRateResponse response = mapCurrent(requestId, baseCurrency, targetCurrency);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(response);
    }

    @Override
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ExchangeRateHistoryResponse> getExchangeRateHistory(String requestId,
                                                                              String baseCurrency,
                                                                              String targetCurrency,
                                                                              OffsetDateTime start,
                                                                              OffsetDateTime end) {
        ExchangeRateHistoryResponse response = mapHistory(requestId, baseCurrency, targetCurrency, start, end);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(response);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateRequestException exception) {
        ApiError error = errorResponse(HttpStatus.CONFLICT, "Duplicate request", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_XML)
                .body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String title = Optional.ofNullable(exception.getReason()).orElse(status.getReasonPhrase());
        ApiError error = errorResponse(status, title, exception.getReason());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_XML)
                .body(error);
    }

    private ExchangeRateResponse mapCurrent(String requestId, String baseCurrency, String targetCurrency) {
        recordRequest(requestId, CURRENT_ENDPOINT);

        String normalizedBase = xmlApiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = xmlApiMapper.normalizeCurrency(targetCurrency);

        ExchangeRate rate = exchangeRateService.findLatest(normalizedBase, normalizedTarget)
                .orElseThrow(() -> notFound(normalizedBase, normalizedTarget));

        return xmlApiMapper.toExchangeRateResponse(rate);
    }

    private ExchangeRateHistoryResponse mapHistory(String requestId,
                                                   String baseCurrency,
                                                   String targetCurrency,
                                                   OffsetDateTime start,
                                                   OffsetDateTime end) {
        recordRequest(requestId, HISTORY_ENDPOINT);

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before or equal to end");
        }

        String normalizedBase = xmlApiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = xmlApiMapper.normalizeCurrency(targetCurrency);

        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();

        List<ExchangeRate> history = exchangeRateService.findHistory(normalizedBase, normalizedTarget, startInstant, endInstant);
        return xmlApiMapper.toHistoryResponse(history);
    }

    private void recordRequest(String requestId, String endpoint) {
        requestLogService.record(xmlApiMapper.toRequestLog(requestId, endpoint, "GET", Instant.now()));
    }

    private ResponseStatusException notFound(String baseCurrency, String targetCurrency) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No exchange rate found for %s/%s".formatted(baseCurrency, targetCurrency));
    }

    private ApiError errorResponse(HttpStatus status, String title, String detail) {
        String safeDetail = Optional.ofNullable(detail).orElse(title);
        return new ApiError()
                .title(title)
                .detail(safeDetail)
                .status(status.value());
    }
}
