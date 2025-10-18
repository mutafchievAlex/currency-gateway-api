package com.example.gateway.api.xml;

import com.example.gateway.api.xml.generated.api.XmlApiApi;
import com.example.gateway.api.xml.generated.model.ErrorResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateCommandRequest;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@Validated
public class XmlApiController implements XmlApiApi {

    private final ExchangeRateService exchangeRateService;
    private final RequestLogService requestLogService;

    public XmlApiController(ExchangeRateService exchangeRateService, RequestLogService requestLogService) {
        this.exchangeRateService = exchangeRateService;
        this.requestLogService = requestLogService;
    }

    @Override
    public ResponseEntity<Object> executeCommand(@Valid ExchangeRateCommandRequest command) {
        recordRequest(command);

        String normalizedBase = XmlApiMapper.normalizeCurrency(command.getBaseCurrency());
        String normalizedTarget = XmlApiMapper.normalizeCurrency(command.getTargetCurrency());

        ExchangeRateCommandRequest.TypeEnum type = command.getType();
        if (type == ExchangeRateCommandRequest.TypeEnum.CURRENT) {
            ExchangeRate rate = exchangeRateService.findLatest(normalizedBase, normalizedTarget)
                    .orElseThrow(() -> notFound(normalizedBase, normalizedTarget));
            ExchangeRateResponse response = XmlApiMapper.toResponse(rate);
            return ResponseEntity.ok(response);
        }

        if (type == ExchangeRateCommandRequest.TypeEnum.HISTORY) {
            Instant start = command.getStart();
            Instant end = command.getEnd();
            if (start != null && end != null && start.isAfter(end)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start must be before or equal to end");
            }

            List<ExchangeRate> history = exchangeRateService.findHistory(
                    normalizedBase,
                    normalizedTarget,
                    start,
                    end
            );
            ExchangeRateHistoryResponse response = XmlApiMapper.toHistoryResponse(history);
            return ResponseEntity.ok(response);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported command type");
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateRequestException exception) {
        ErrorResponse error = new ErrorResponse();
        error.setTitle("Duplicate request");
        error.setDetail(exception.getMessage());
        error.setStatus(HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_XML)
                .body(error);
    }

    private void recordRequest(ExchangeRateCommandRequest command) {
        requestLogService.record(XmlApiMapper.toRequestLog(command, "/xml_api/command", Instant.now()));
    }

    private ResponseStatusException notFound(String baseCurrency, String targetCurrency) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No exchange rate found for %s/%s".formatted(baseCurrency, targetCurrency));
    }
}
