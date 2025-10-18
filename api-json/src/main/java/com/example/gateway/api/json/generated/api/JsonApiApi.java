package com.example.gateway.api.json.generated.api;

import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
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
import java.util.List;

@Validated
@RequestMapping("/json_api")
public interface JsonApiApi {

    @GetMapping(value = "/current", produces = {"application/json"})
    ResponseEntity<ExchangeRateResponse> getCurrentRate(
            @RequestParam("requestId") @NotNull @Size(min = 1, max = 100) String requestId,
            @RequestParam("baseCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String baseCurrency,
            @RequestParam("targetCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String targetCurrency);

    @GetMapping(value = "/history", produces = {"application/json"})
    ResponseEntity<List<ExchangeRateResponse>> getHistory(
            @RequestParam("requestId") @NotNull @Size(min = 1, max = 100) String requestId,
            @RequestParam("baseCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String baseCurrency,
            @RequestParam("targetCurrency") @NotNull @Pattern(regexp = "^[A-Za-z]{3}$") String targetCurrency,
            @RequestParam("start") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end);
}
