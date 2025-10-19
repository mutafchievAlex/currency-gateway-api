package com.example.gateway.api.json;

import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JsonExchangeRateController.class)
@Import(JsonExchangeRateControllerTest.MapperConfig.class)
class JsonExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private RequestLogService requestLogService;

    @Test
    void returnsCurrentRateAsJson() throws Exception {
        Instant timestamp = Instant.parse("2024-03-15T10:15:30Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.9200"), timestamp);
        when(exchangeRateService.findLatest("USD", "EUR")).thenReturn(Optional.of(rate));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseCurrency").value("USD"))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"))
                .andExpect(jsonPath("$.rate").value("0.9200"))
                .andExpect(jsonPath("$.timestamp").value("2024-03-15T10:15:30Z"));

        ArgumentCaptor<RequestLog> captor = ArgumentCaptor.forClass(RequestLog.class);
        verify(requestLogService).record(captor.capture());
        assertThat(captor.getValue().requestId()).isEqualTo("req-123");
        assertThat(captor.getValue().endpoint()).isEqualTo("/api/exchange-rates/current");
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        when(exchangeRateService.findLatest("USD", "EUR")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        doThrow(new DuplicateRequestException("duplicate"))
                .when(requestLogService).record(any(RequestLog.class));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate request"))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()));
    }

    @Test
    void returnsHistoryAsJson() throws Exception {
        Instant now = Instant.parse("2024-03-15T10:15:30Z");
        List<ExchangeRate> history = List.of(
                new ExchangeRate("USD", "JPY", new BigDecimal("110.00"), now.minus(2, ChronoUnit.HOURS)),
                new ExchangeRate("USD", "JPY", new BigDecimal("111.00"), now.minus(1, ChronoUnit.HOURS))
        );
        when(exchangeRateService.findHistory("USD", "JPY",
                now.minus(3, ChronoUnit.HOURS), now)).thenReturn(history);

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-456")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", now.minus(3, ChronoUnit.HOURS).toString())
                        .queryParam("end", now.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rates[0].targetCurrency").value("JPY"))
                .andExpect(jsonPath("$.rates[1].rate").value("111.00"));

        verify(requestLogService, Mockito.atLeastOnce()).record(any(RequestLog.class));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
        Instant start = Instant.parse("2024-03-15T10:15:30Z");
        Instant end = start.minusSeconds(60);

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-456")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", start.toString())
                        .queryParam("end", end.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class MapperConfig {

        @Bean
        JsonApiMapper jsonApiMapper() {
            return Mappers.getMapper(JsonApiMapper.class);
        }
    }
}
