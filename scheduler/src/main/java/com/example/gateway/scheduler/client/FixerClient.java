package com.example.gateway.scheduler.client;

import com.example.gateway.domain.repository.ExternalRatesClient;
import com.example.gateway.domain.repository.RatesSnapshot;
import com.example.gateway.domain.validation.BeanValidationService;
import com.example.gateway.common.validation.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * HTTP client that retrieves currency exchange rates from the Fixer API.
 */
@Component
public class FixerClient implements ExternalRatesClient {

    private final RestClient restClient;
    private final String apiKey;
    private final BeanValidationService validationService;

    public FixerClient(RestClient.Builder builder,
                       @Value("${fixer.url:https://data.fixer.io/api}") String baseUrl,
                       @Value("${fixer.api-key}") String apiKey,
                       BeanValidationService validationService) {
        this.validationService = validationService;
        String normalizedBaseUrl = ValidationUtils.requireTrimmedNotBlank(baseUrl, "baseUrl");
        this.restClient = builder.baseUrl(normalizedBaseUrl).build();
        this.apiKey = ValidationUtils.requireTrimmedNotBlank(apiKey, "apiKey");
    }

    @Override
    public RatesSnapshot fetchLatestRates(String baseCurrency, Collection<String> targetCurrencies) {
        String normalizedBase = ValidationUtils.requireTrimmedNotBlank(baseCurrency, "baseCurrency").toUpperCase(Locale.ROOT);
        Collection<String> safeTargets = validationService.requirePresent(targetCurrencies, "targetCurrencies");

        Collection<String> normalizedTargets = safeTargets.stream()
                .filter(Objects::nonNull)
                .map(currency -> currency.trim().toUpperCase(Locale.ROOT))
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toSet());

        FixerLatestResponse response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/latest")
                            .queryParam("access_key", apiKey)
                            .queryParam("base", normalizedBase);
                    if (!normalizedTargets.isEmpty()) {
                        uriBuilder.queryParam("symbols", String.join(",", normalizedTargets));
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(FixerLatestResponse.class);

        if (response == null) {
            throw new IllegalStateException("Fixer API returned an empty response");
        }

        return response.toSnapshot();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FixerLatestResponse(boolean success,
                                       @JsonProperty("base") String baseCurrency,
                                       @JsonProperty("timestamp") long epochSeconds,
                                       Map<String, BigDecimal> rates) {

        RatesSnapshot toSnapshot() {
            if (!success) {
                throw new IllegalStateException("Fixer API indicated an unsuccessful response");
            }
            if (baseCurrency == null || baseCurrency.isBlank()) {
                throw new IllegalStateException("Fixer API response did not include a base currency");
            }
            if (rates == null) {
                throw new IllegalStateException("Fixer API response did not include rate data");
            }
            return new RatesSnapshot(baseCurrency.trim().toUpperCase(Locale.ROOT), Instant.ofEpochSecond(epochSeconds), rates);
        }
    }
}
