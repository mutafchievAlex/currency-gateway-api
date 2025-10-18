package com.example.gateway.api.json;

import com.example.gateway.api.json.generated.api.JsonApiApi;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@Validated
public class JsonApiController implements JsonApiApi {

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;

    public JsonApiController(ExchangeRateService exchangeRateService, RequestLogService requestLogService) {
        this.exchangeRateService = exchangeRateService;
        this.requestLogService = requestLogService;
    }

    @Override
    public ResponseEntity<ExchangeRateResponse> getCurrentRate(String requestId,
                                                               String baseCurrency,
                                                               String targetCurrency) {
        recordRequest(requestId, "/json_api/current");
        String normalizedBase = JsonApiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = JsonApiMapper.normalizeCurrency(targetCurrency);
        ExchangeRate rate = exchangeRateService.findLatest(normalizedBase, normalizedTarget)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No exchange rate found for %s/%s".formatted(normalizedBase, normalizedTarget)));
        return ResponseEntity.ok(JsonApiMapper.toResponse(rate));
    }

    @Override
    public ResponseEntity<List<ExchangeRateResponse>> getHistory(String requestId,
                                                                 String baseCurrency,
                                                                 String targetCurrency,
                                                                 Instant start,
                                                                 Instant end) {
        recordRequest(requestId, "/json_api/history");
        if (start != null && end != null && start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before or equal to end");
        }
        String normalizedBase = JsonApiMapper.normalizeCurrency(baseCurrency);
        String normalizedTarget = JsonApiMapper.normalizeCurrency(targetCurrency);
        List<ExchangeRate> history = exchangeRateService.findHistory(
                normalizedBase,
                normalizedTarget,
                start,
                end);
        return ResponseEntity.ok(JsonApiMapper.toResponseList(history));
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateRequestException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Duplicate request");
        problem.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    private void recordRequest(String requestId, String endpoint) {
        requestLogService.record(JsonApiMapper.toRequestLog(requestId, endpoint, "GET", Instant.now()));
    }
}
