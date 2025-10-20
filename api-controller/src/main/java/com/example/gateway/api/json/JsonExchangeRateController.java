package com.example.gateway.api.json;

import com.example.gateway.api.json.generated.api.JsonExchangeRatesApi;
import com.example.gateway.api.json.generated.model.ApiError;
import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.api.support.ExchangeRateControllerSupport;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/exchange-rates", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class JsonExchangeRateController implements JsonExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateControllerSupport<ExchangeRateResponse, ExchangeRateHistoryResponse, ApiError> support;

    public JsonExchangeRateController(ExchangeRateService exchangeRateService,
                                      RequestLogService requestLogService,
                                      JsonApiMapper jsonApiMapper) {
        Objects.requireNonNull(exchangeRateService, "exchangeRateService must not be null");
        Objects.requireNonNull(requestLogService, "requestLogService must not be null");
        Objects.requireNonNull(jsonApiMapper, "jsonApiMapper must not be null");
        this.support = new ExchangeRateControllerSupport<>(exchangeRateService, requestLogService, jsonApiMapper);
    }

    @Override
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRateResponse> getCurrentExchangeRate(String requestId,
                                                                       String baseCurrency,
                                                                       String targetCurrency) {
        ExchangeRateResponse response = support.currentRate(requestId, CURRENT_ENDPOINT, baseCurrency, targetCurrency);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExchangeRateHistoryResponse> getExchangeRateHistory(String requestId,
                                                                              String baseCurrency,
                                                                              String targetCurrency,
                                                                              OffsetDateTime start,
                                                                              OffsetDateTime end) {
        ExchangeRateHistoryResponse response = support.history(requestId, HISTORY_ENDPOINT, baseCurrency, targetCurrency, start, end);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateRequestException exception) {
        ApiError error = support.duplicateError(exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        ApiError error = support.statusError(exception);
        return ResponseEntity.status(status).body(error);
    }
}
