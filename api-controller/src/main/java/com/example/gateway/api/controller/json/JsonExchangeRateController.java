package com.example.gateway.api.controller.json;

import com.example.gateway.api.json.generated.api.JsonExchangeRatesApi;
import com.example.gateway.api.json.generated.model.JsonClientMetadata;
import com.example.gateway.api.json.generated.model.JsonCurrencyPair;
import com.example.gateway.api.json.generated.model.JsonCurrentRequest;
import com.example.gateway.api.json.generated.model.JsonCurrentResponse;
import com.example.gateway.api.json.generated.model.JsonHistoryPeriod;
import com.example.gateway.api.json.generated.model.JsonHistoryRequest;
import com.example.gateway.api.json.generated.model.JsonHistoryResponse;
import com.example.gateway.api.json.generated.model.JsonHistoryWindow;
import com.example.gateway.api.mapper.json.JsonApiMapper;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
@Validated
public class JsonExchangeRateController implements JsonExchangeRatesApi {

    private static final String CURRENT_ENDPOINT = "/json_api/current";
    private static final String HISTORY_ENDPOINT = "/json_api/history";

    private final ExchangeRateQueryService exchangeRateQueryService;
    private final JsonApiMapper mapper;

    public JsonExchangeRateController(ExchangeRateQueryService exchangeRateQueryService,
                                      JsonApiMapper mapper) {
        this.exchangeRateQueryService = exchangeRateQueryService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<JsonCurrentResponse> getCurrentExchangeRate(@Valid JsonCurrentRequest request) {
        UUID requestId = request.getRequestId();
        OffsetDateTime requestedAt = request.getTimestamp();
        JsonClientMetadata client = request.getClient();
        JsonCurrencyPair currency = request.getCurrency();

        ExchangeRate rate = exchangeRateQueryService.getCurrentRate(
                requestId,
                CURRENT_ENDPOINT,
                requestedAt.toInstant(),
                client.getId(),
                currency.getBase(),
                currency.getTarget());

        JsonCurrentResponse response = new JsonCurrentResponse()
                .requestId(requestId)
                .requestedAt(requestedAt)
                .client(copyClient(client))
                .currency(copyCurrency(currency))
                .quote(mapper.toQuote(rate));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, CURRENT_ENDPOINT)
                .body(response);
    }

    @Override
    public ResponseEntity<JsonHistoryResponse> getExchangeRateHistory(@Valid JsonHistoryRequest request) {
        UUID requestId = request.getRequestId();
        OffsetDateTime requestedAt = request.getTimestamp();
        JsonClientMetadata client = request.getClient();
        JsonCurrencyPair currency = request.getCurrency();
        JsonHistoryPeriod period = request.getPeriod();

        OffsetDateTime windowEnd = requestedAt;
        OffsetDateTime windowStart = requestedAt.minus(resolveDuration(period));

        List<ExchangeRate> history = exchangeRateQueryService.getHistory(
                requestId,
                HISTORY_ENDPOINT,
                requestedAt.toInstant(),
                client.getId(),
                currency.getBase(),
                currency.getTarget(),
                windowStart,
                windowEnd);

        JsonHistoryResponse response = new JsonHistoryResponse()
                .requestId(requestId)
                .requestedAt(requestedAt)
                .client(copyClient(client))
                .currency(copyCurrency(currency))
                .period(copyPeriod(period))
                .window(new JsonHistoryWindow()
                        .start(windowStart)
                        .end(windowEnd))
                .quotes(mapper.toQuotes(history));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, HISTORY_ENDPOINT)
                .body(response);
    }

    private Duration resolveDuration(JsonHistoryPeriod period) {
        JsonHistoryPeriod.UnitEnum unit = Objects.requireNonNull(period.getUnit(), "unit");
        int amount = Objects.requireNonNull(period.getAmount(), "amount");
        return switch (unit) {
            case MINUTES -> Duration.ofMinutes(amount);
            case HOURS -> Duration.ofHours(amount);
            case DAYS -> Duration.ofDays(amount);
        };
    }

    private JsonClientMetadata copyClient(JsonClientMetadata client) {
        return new JsonClientMetadata()
                .id(trimIfPresent(client.getId()))
                .name(client.getName())
                .version(client.getVersion());
    }

    private JsonCurrencyPair copyCurrency(JsonCurrencyPair currency) {
        return new JsonCurrencyPair()
                .base(currency.getBase())
                .target(currency.getTarget());
    }

    private JsonHistoryPeriod copyPeriod(JsonHistoryPeriod period) {
        return new JsonHistoryPeriod()
                .amount(period.getAmount())
                .unit(period.getUnit());
    }

    private String trimIfPresent(String value) {
        return value == null ? null : value.trim();
    }
}
