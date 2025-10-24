package com.example.gateway.api.controller.json;

import com.example.gateway.api.json.generated.model.JsonClientMetadata;
import com.example.gateway.api.json.generated.model.JsonCurrencyPair;
import com.example.gateway.api.json.generated.model.JsonCurrentRequest;
import com.example.gateway.api.json.generated.model.JsonHistoryPeriod;
import com.example.gateway.api.json.generated.model.JsonHistoryRequest;
import com.example.gateway.api.json.generated.model.JsonQuote;
import com.example.gateway.api.mapper.json.JsonApiMapper;
import com.example.gateway.api.support.ExchangeRateTestFixtures;
import com.example.gateway.domain.exception.DuplicateRequestException;
import com.example.gateway.domain.exception.ExchangeRateNotFoundException;
import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JsonExchangeRateController.class)
class JsonExchangeRateControllerTest {

    private static final OffsetDateTime REQUESTED_AT = ExchangeRateTestFixtures.TIMESTAMP.atOffset(ZoneOffset.UTC);
    private static final UUID CURRENT_REQUEST_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID HISTORY_REQUEST_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExchangeRateQueryService exchangeRateQueryService;

    @MockBean
    private JsonApiMapper jsonApiMapper;

    @Test
    void returnsCurrentRateAsJson() throws Exception {
        ExchangeRate rate = ExchangeRateTestFixtures.rate().build();
        JsonQuote quote = new JsonQuote()
                .provider(JsonQuote.ProviderEnum.FIXER)
                .rate("0.9200")
                .timestamp(REQUESTED_AT);

        JsonCurrentRequest request = defaultCurrentRequest();

        when(exchangeRateQueryService.getCurrentRate(
                CURRENT_REQUEST_ID,
                "/json_api/current",
                REQUESTED_AT.toInstant(),
                "mobile-app",
                "USD",
                "EUR"))
                .thenReturn(rate);
        when(jsonApiMapper.toQuote(rate)).thenReturn(quote);

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Location", "/json_api/current"))
                .andExpect(jsonPath("$.requestId").value(CURRENT_REQUEST_ID.toString()))
                .andExpect(jsonPath("$.quote.rate").value("0.9200"))
                .andExpect(jsonPath("$.quote.provider").value("Fixer"));

        verify(exchangeRateQueryService).getCurrentRate(
                CURRENT_REQUEST_ID,
                "/json_api/current",
                REQUESTED_AT.toInstant(),
                "mobile-app",
                "USD",
                "EUR");
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        JsonCurrentRequest request = defaultCurrentRequest();

        when(exchangeRateQueryService.getCurrentRate(
                CURRENT_REQUEST_ID,
                "/json_api/current",
                REQUESTED_AT.toInstant(),
                "mobile-app",
                "USD",
                "EUR"))
                .thenThrow(new ExchangeRateNotFoundException("USD", "EUR"));

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        JsonCurrentRequest request = defaultCurrentRequest();

        when(exchangeRateQueryService.getCurrentRate(
                CURRENT_REQUEST_ID,
                "/json_api/current",
                REQUESTED_AT.toInstant(),
                "mobile-app",
                "USD",
                "EUR"))
                .thenThrow(new DuplicateRequestException("duplicate"));

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("Duplicate request"))
                .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()));
    }

    @Test
    void returnsBadRequestWhenMandatoryValueMissing() throws Exception {
        JsonCurrentRequest request = defaultCurrentRequest();

        when(exchangeRateQueryService.getCurrentRate(
                CURRENT_REQUEST_ID,
                "/json_api/current",
                REQUESTED_AT.toInstant(),
                "mobile-app",
                "USD",
                "EUR"))
                .thenThrow(new MissingRequiredValueException("baseCurrency must not be null"));

        mockMvc.perform(post("/json_api/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("Missing required value"))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void returnsHistoryAsJson() throws Exception {
        ExchangeRateTestFixtures.HistoryScenario scenario = ExchangeRateTestFixtures.usdToJpyHistoryScenario();
        OffsetDateTime requestedAt = scenario.end().atOffset(ZoneOffset.UTC);
        OffsetDateTime windowStart = requestedAt.minusHours(3);

        JsonQuote firstQuote = new JsonQuote()
                .provider(JsonQuote.ProviderEnum.FIXER)
                .rate("110.00")
                .timestamp(scenario.start().plus(1, java.time.temporal.ChronoUnit.HOURS).atOffset(ZoneOffset.UTC));
        JsonQuote secondQuote = new JsonQuote()
                .provider(JsonQuote.ProviderEnum.FIXER)
                .rate("111.00")
                .timestamp(scenario.end().minus(1, java.time.temporal.ChronoUnit.HOURS).atOffset(ZoneOffset.UTC));

        JsonHistoryRequest request = defaultHistoryRequest();
        request.timestamp(requestedAt);

        when(exchangeRateQueryService.getHistory(
                HISTORY_REQUEST_ID,
                "/json_api/history",
                requestedAt.toInstant(),
                "analytics-service",
                "USD",
                "JPY",
                windowStart,
                requestedAt)).thenReturn(scenario.rates());
        when(jsonApiMapper.toQuotes(anyList())).thenReturn(List.of(firstQuote, secondQuote));

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Location", "/json_api/history"))
                .andExpect(jsonPath("$.period.amount").value(3))
                .andExpect(jsonPath("$.quotes[1].rate").value("111.00"))
                .andExpect(jsonPath("$.window.start").value(windowStart.toString()));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
        JsonHistoryRequest request = defaultHistoryRequest();
        OffsetDateTime requestedAt = request.getTimestamp();
        OffsetDateTime windowStart = requestedAt.minusHours(3);

        when(exchangeRateQueryService.getHistory(
                HISTORY_REQUEST_ID,
                "/json_api/history",
                requestedAt.toInstant(),
                "analytics-service",
                "USD",
                "JPY",
                windowStart,
                requestedAt)).thenThrow(new InvalidExchangeRateQueryException("start must be before or equal to end"));

        mockMvc.perform(post("/json_api/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    private JsonCurrentRequest defaultCurrentRequest() {
        JsonClientMetadata client = new JsonClientMetadata()
                .id("mobile-app")
                .name("Mobile App");
        JsonCurrencyPair currency = new JsonCurrencyPair()
                .base("USD")
                .target("EUR");
        return new JsonCurrentRequest()
                .requestId(CURRENT_REQUEST_ID)
                .timestamp(REQUESTED_AT)
                .client(client)
                .currency(currency);
    }

    private JsonHistoryRequest defaultHistoryRequest() {
        JsonClientMetadata client = new JsonClientMetadata()
                .id("analytics-service")
                .name("Analytics");
        JsonCurrencyPair currency = new JsonCurrencyPair()
                .base("USD")
                .target("JPY");
        JsonHistoryPeriod period = new JsonHistoryPeriod()
                .amount(3)
                .unit(JsonHistoryPeriod.UnitEnum.HOURS);
        return new JsonHistoryRequest()
                .requestId(HISTORY_REQUEST_ID)
                .timestamp(REQUESTED_AT)
                .client(client)
                .currency(currency)
                .period(period);
    }
}
