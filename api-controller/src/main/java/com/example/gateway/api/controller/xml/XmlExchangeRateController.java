package com.example.gateway.api.controller.xml;

import com.example.gateway.api.mapper.xml.XmlApiMapper;
import com.example.gateway.api.xml.generated.api.XmlExchangeRatesApi;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/exchange-rates", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
@Validated
public class XmlExchangeRateController implements XmlExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateQueryService exchangeRateQueryService;
    private final XmlApiMapper mapper;

    public XmlExchangeRateController(ExchangeRateQueryService exchangeRateQueryService,
                                     XmlApiMapper mapper) {
        this.exchangeRateQueryService = exchangeRateQueryService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<ExchangeRateResponse> getCurrentExchangeRate(String requestId,
                                                                       String baseCurrency,
                                                                       String targetCurrency) {
        ExchangeRate rate = exchangeRateQueryService.getCurrentRate(requestId, CURRENT_ENDPOINT, baseCurrency, targetCurrency);
        ExchangeRateResponse body = mapper.toExchangeRateResponse(rate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, CURRENT_ENDPOINT)
                .body(body);
    }

    @Override
    public ResponseEntity<ExchangeRateHistoryResponse> getExchangeRateHistory(String requestId,
                                                                              String baseCurrency,
                                                                              String targetCurrency,
                                                                              OffsetDateTime start,
                                                                              OffsetDateTime end) {
        List<ExchangeRate> history = exchangeRateQueryService.getHistory(requestId, HISTORY_ENDPOINT, baseCurrency, targetCurrency, start, end);
        ExchangeRateHistoryResponse body = mapper.toHistoryResponse(history);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, HISTORY_ENDPOINT)
                .body(body);
    }
}
