package com.example.gateway.api.controller.json;

import com.example.gateway.api.mapper.json.JsonApiMapper;
import com.example.gateway.api.support.ExchangeRateTestFixtures;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.domain.exception.ExchangeRateNotFoundException;
import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JsonExchangeRateController.class)
@Import(JsonExchangeRateControllerTest.MapperConfig.class)
class JsonExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateQueryService exchangeRateQueryService;

    @Test
    void returnsCurrentRateAsJson() throws Exception {
        ExchangeRate rate = ExchangeRateTestFixtures.rate().build();
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenReturn(rate);

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Location", "/api/exchange-rates/current"))
                .andExpect(jsonPath("$.baseCurrency").value("USD"))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"))
                .andExpect(jsonPath("$.rate").value("0.9200"));

        ArgumentCaptor<String> baseCaptor = ArgumentCaptor.forClass(String.class);
        verify(exchangeRateQueryService).getCurrentRate("req-123", "/api/exchange-rates/current", baseCaptor.capture(), any());
        assertThat(baseCaptor.getValue()).isEqualTo("usd");
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenThrow(new ExchangeRateNotFoundException("USD", "EUR"));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.type").value(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenThrow(new DuplicateRequestException("duplicate"));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("Duplicate request"))
                .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()));
    }

    @Test
    void returnsBadRequestWhenMandatoryValueMissing() throws Exception {
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenThrow(new MissingRequiredValueException("baseCurrency must not be null"));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("Missing required value"))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void returnsHistoryAsJson() throws Exception {
        ExchangeRateTestFixtures.HistoryScenario scenario = ExchangeRateTestFixtures.usdToJpyHistoryScenario();
        when(exchangeRateQueryService.getHistory("req-456", "/api/exchange-rates/history", "usd", "jpy",
                scenario.start().atOffset(java.time.ZoneOffset.UTC),
                scenario.end().atOffset(java.time.ZoneOffset.UTC))).thenReturn(scenario.rates());

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-456")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", scenario.start().toString())
                        .queryParam("end", scenario.end().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Location", "/api/exchange-rates/history"))
                .andExpect(jsonPath("$.rates[0].targetCurrency").value("JPY"))
                .andExpect(jsonPath("$.rates[1].rate").value("111.00"));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
        when(exchangeRateQueryService.getHistory("req-456", "/api/exchange-rates/history", "usd", "jpy",
                ExchangeRateTestFixtures.TIMESTAMP.atOffset(java.time.ZoneOffset.UTC),
                ExchangeRateTestFixtures.TIMESTAMP.minusSeconds(60).atOffset(java.time.ZoneOffset.UTC)))
                .thenThrow(new InvalidExchangeRateQueryException("start must be before or equal to end"));

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-456")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", ExchangeRateTestFixtures.TIMESTAMP.toString())
                        .queryParam("end", ExchangeRateTestFixtures.TIMESTAMP.minusSeconds(60).toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @TestConfiguration
    static class MapperConfig {

        @Bean
        JsonApiMapper jsonApiMapper() {
            return Mappers.getMapper(JsonApiMapper.class);
        }
    }
}
