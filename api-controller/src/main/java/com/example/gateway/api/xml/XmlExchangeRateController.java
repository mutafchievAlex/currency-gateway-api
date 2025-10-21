package com.example.gateway.api.xml;

import com.example.gateway.api.support.ExchangeRateControllerSupport;
import com.example.gateway.api.support.JaxRsResponseEntityAdapter;
import com.example.gateway.api.xml.generated.api.XmlExchangeRatesApi;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.api.xml.mapper.XmlApiMapper;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.application.validation.BeanValidationService;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(value = "/api/exchange-rates", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
@Validated
public class XmlExchangeRateController implements XmlExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/api/exchange-rates/current";
    private static final String HISTORY_ENDPOINT = "/api/exchange-rates/history";

    private final ExchangeRateControllerSupport<ExchangeRateResponse, ExchangeRateHistoryResponse> support;

    public XmlExchangeRateController(ExchangeRateService exchangeRateService,
                                     RequestLogService requestLogService,
                                     XmlApiMapper xmlApiMapper,
                                     BeanValidationService validationService) {
        this.support = new ExchangeRateControllerSupport<>(exchangeRateService, requestLogService, xmlApiMapper, validationService);
    }

    @Override
    @GetMapping(value = "/current", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ExchangeRateResponse> getCurrentExchangeRate(String requestId,
                                                                       String baseCurrency,
                                                                       String targetCurrency) {
        ExchangeRateResponse response = support.currentRate(requestId, CURRENT_ENDPOINT, baseCurrency, targetCurrency);
        Response jaxRsResponse = Response.ok(response)
                .header(HttpHeaders.CONTENT_LOCATION, CURRENT_ENDPOINT)
                .type(MediaType.APPLICATION_XML)
                .build();
        return JaxRsResponseEntityAdapter.toResponseEntity(jaxRsResponse);
    }

    @Override
    @GetMapping(value = "/history", produces = org.springframework.http.MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<ExchangeRateHistoryResponse> getExchangeRateHistory(String requestId,
                                                                              String baseCurrency,
                                                                              String targetCurrency,
                                                                              OffsetDateTime start,
                                                                              OffsetDateTime end) {
        ExchangeRateHistoryResponse response = support.history(requestId, HISTORY_ENDPOINT, baseCurrency, targetCurrency, start, end);
        Response jaxRsResponse = Response.ok(response)
                .header(HttpHeaders.CONTENT_LOCATION, HISTORY_ENDPOINT)
                .type(MediaType.APPLICATION_XML)
                .build();
        return JaxRsResponseEntityAdapter.toResponseEntity(jaxRsResponse);
    }
}
