package com.example.gateway.api.controller.json;

import com.example.gateway.api.controller.ResponseEntityAdapter;
import com.example.gateway.api.json.generated.api.JsonExchangeRatesApi;
import com.example.gateway.api.json.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.api.mapper.json.JsonApiMapper;
import com.example.gateway.application.ExchangeRateQueryApplicationService;
import com.example.gateway.domain.ExchangeRate;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@Validated
public class JsonExchangeRateController implements JsonExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateQueryApplicationService exchangeRateQueryService;
    private final JsonApiMapper mapper;

    public JsonExchangeRateController(ExchangeRateQueryApplicationService exchangeRateQueryService,
                                      JsonApiMapper mapper) {
        this.exchangeRateQueryService = exchangeRateQueryService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<ExchangeRateResponse> getCurrentExchangeRate(String requestId,
                                                                       String baseCurrency,
                                                                       String targetCurrency) {
        ExchangeRate rate = exchangeRateQueryService.getCurrentRate(requestId, CURRENT_ENDPOINT, baseCurrency, targetCurrency);
        ExchangeRateResponse body = mapper.toExchangeRateResponse(rate);
        Response response = Response.ok(body)
                .header(HttpHeaders.CONTENT_LOCATION, CURRENT_ENDPOINT)
                .type(MediaType.APPLICATION_JSON)
                .build();
        return ResponseEntityAdapter.from(response);
    }

    @Override
    public ResponseEntity<ExchangeRateHistoryResponse> getExchangeRateHistory(String requestId,
                                                                              String baseCurrency,
                                                                              String targetCurrency,
                                                                              OffsetDateTime start,
                                                                              OffsetDateTime end) {
        List<ExchangeRate> history = exchangeRateQueryService.getHistory(requestId, HISTORY_ENDPOINT, baseCurrency, targetCurrency, start, end);
        ExchangeRateHistoryResponse body = mapper.toHistoryResponse(history);
        Response response = Response.ok(body)
                .header(HttpHeaders.CONTENT_LOCATION, HISTORY_ENDPOINT)
                .type(MediaType.APPLICATION_JSON)
                .build();
        return ResponseEntityAdapter.from(response);
    }
}
