package com.example.gateway.api.xml;

import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(XmlExchangeRateController.class)
@Import(XmlExchangeRateControllerTest.MapperConfig.class)
class XmlExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private RequestLogService requestLogService;

    @Test
    void returnsCurrentRateAsXml() throws Exception {
        Instant timestamp = Instant.parse("2024-03-15T10:15:30Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.9200"), timestamp);
        when(exchangeRateService.findLatest("USD", "EUR")).thenReturn(Optional.of(rate));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-xml-1")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/exchangeRate/baseCurrency").string("USD"))
                .andExpect(xpath("/exchangeRate/targetCurrency").string("EUR"))
                .andExpect(xpath("/exchangeRate/rate").string("0.9200"))
                .andExpect(xpath("/exchangeRate/timestamp").string("2024-03-15T10:15:30Z"));

        verify(requestLogService).record(any(RequestLog.class));
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        doThrow(new DuplicateRequestException("duplicate"))
                .when(requestLogService).record(any(RequestLog.class));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-xml-1")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isConflict())
                .andExpect(xpath("/error/title").string("Duplicate request"))
                .andExpect(xpath("/error/status").string(String.valueOf(HttpStatus.CONFLICT.value())));
    }

    @Test
    void returnsHistoryAsXml() throws Exception {
        Instant now = Instant.parse("2024-03-15T10:15:30Z");
        List<ExchangeRate> history = List.of(
                new ExchangeRate("USD", "JPY", new BigDecimal("110.00"), now.minus(2, ChronoUnit.HOURS)),
                new ExchangeRate("USD", "JPY", new BigDecimal("111.00"), now.minus(1, ChronoUnit.HOURS))
        );
        when(exchangeRateService.findHistory("USD", "JPY",
                now.minus(3, ChronoUnit.HOURS), now)).thenReturn(history);

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-xml-2")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", now.minus(3, ChronoUnit.HOURS).toString())
                        .queryParam("end", now.toString())
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(xpath("/exchangeRateHistory/rates/rate[1]/targetCurrency").string("JPY"))
                .andExpect(xpath("/exchangeRateHistory/rates/rate[2]/rate").string("111.00"));

        verify(requestLogService, Mockito.atLeastOnce()).record(any(RequestLog.class));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
        Instant start = Instant.parse("2024-03-15T10:15:30Z");
        Instant end = start.minusSeconds(60);

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-xml-2")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", start.toString())
                        .queryParam("end", end.toString())
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class MapperConfig {

        @Bean
        XmlApiMapper xmlApiMapper() {
            return Mappers.getMapper(XmlApiMapper.class);
        }
    }
}
