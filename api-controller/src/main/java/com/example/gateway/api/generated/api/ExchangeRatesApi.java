package com.example.gateway.api.generated.api;

import com.example.gateway.api.generated.model.ExchangeRateHistoryRepresentation;
import com.example.gateway.api.generated.model.ExchangeRateRepresentation;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@RequestMapping("/api/exchange-rates")
public interface ExchangeRatesApi {

    @GetMapping(value = "/current", produces = {"application/json", "application/xml"})
    ResponseEntity<ExchangeRateRepresentation> getCurrentExchangeRate(
            @RequestParam("requestId") @NotNull @Size(min = 1, max = 100) String requestId,
            @RequestParam("baseCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String baseCurrency,
            @RequestParam("targetCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String targetCurrency);

    @GetMapping(value = "/history", produces = {"application/json", "application/xml"})
    ResponseEntity<ExchangeRateHistoryRepresentation> getExchangeRateHistory(
            @RequestParam("requestId") @NotNull @Size(min = 1, max = 100) String requestId,
            @RequestParam("baseCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String baseCurrency,
            @RequestParam("targetCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String targetCurrency,
            @RequestParam("start") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end);
}
