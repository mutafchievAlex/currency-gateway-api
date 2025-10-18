package com.example.gateway.api.xml;

import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(XmlApiController.class)
class XmlApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private RequestLogService requestLogService;

    @Test
    void returnsCurrentRate() throws Exception {
        Instant timestamp = Instant.parse("2024-03-15T10:15:30Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.9200"), timestamp);
        when(exchangeRateService.findLatest("USD", "EUR")).thenReturn(Optional.of(rate));

        String payload = """
                <exchangeRateCommand>
                    <requestId>req-123</requestId>
                    <type>CURRENT</type>
                    <baseCurrency>usd</baseCurrency>
                    <targetCurrency>eur</targetCurrency>
                </exchangeRateCommand>
                """;

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(xpath("/exchangeRateResponse/baseCurrency").string("USD"))
                .andExpect(xpath("/exchangeRateResponse/targetCurrency").string("EUR"))
                .andExpect(xpath("/exchangeRateResponse/rate").string("0.9200"))
                .andExpect(xpath("/exchangeRateResponse/timestamp").string("2024-03-15T10:15:30Z"));

        ArgumentCaptor<RequestLog> captor = ArgumentCaptor.forClass(RequestLog.class);
        verify(requestLogService).record(captor.capture());
        assertThat(captor.getValue().requestId()).isEqualTo("req-123");
        assertThat(captor.getValue().endpoint()).isEqualTo("/xml_api/command");
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        when(exchangeRateService.findLatest("USD", "EUR")).thenReturn(Optional.empty());

        String payload = """
                <exchangeRateCommand>
                    <requestId>req-123</requestId>
                    <type>CURRENT</type>
                    <baseCurrency>usd</baseCurrency>
                    <targetCurrency>eur</targetCurrency>
                </exchangeRateCommand>
                """;

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        doThrow(new DuplicateRequestException("duplicate"))
                .when(requestLogService).record(any(RequestLog.class));

        String payload = """
                <exchangeRateCommand>
                    <requestId>req-123</requestId>
                    <type>CURRENT</type>
                    <baseCurrency>usd</baseCurrency>
                    <targetCurrency>eur</targetCurrency>
                </exchangeRateCommand>
                """;

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(xpath("/error/title").string("Duplicate request"));
    }

    @Test
    void returnsHistory() throws Exception {
        Instant now = Instant.parse("2024-03-15T10:15:30Z");
        List<ExchangeRate> history = List.of(
                new ExchangeRate("USD", "JPY", new BigDecimal("110.00"), now.minus(2, ChronoUnit.HOURS)),
                new ExchangeRate("USD", "JPY", new BigDecimal("111.00"), now.minus(1, ChronoUnit.HOURS))
        );
        when(exchangeRateService.findHistory("USD", "JPY",
                now.minus(3, ChronoUnit.HOURS), now)).thenReturn(history);

        String payload = """
                <exchangeRateCommand>
                    <requestId>req-456</requestId>
                    <type>HISTORY</type>
                    <baseCurrency>usd</baseCurrency>
                    <targetCurrency>jpy</targetCurrency>
                    <start>%s</start>
                    <end>%s</end>
                </exchangeRateCommand>
                """.formatted(now.minus(3, ChronoUnit.HOURS), now);

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(xpath("/exchangeRateHistoryResponse/rates/rate[1]/targetCurrency").string("JPY"))
                .andExpect(xpath("/exchangeRateHistoryResponse/rates/rate[2]/rate").string("111.00"));

        verify(requestLogService, Mockito.atLeastOnce()).record(any(RequestLog.class));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
        Instant start = Instant.parse("2024-03-15T10:15:30Z");
        Instant end = start.minusSeconds(60);

        String payload = """
                <exchangeRateCommand>
                    <requestId>req-456</requestId>
                    <type>HISTORY</type>
                    <baseCurrency>usd</baseCurrency>
                    <targetCurrency>jpy</targetCurrency>
                    <start>%s</start>
                    <end>%s</end>
                </exchangeRateCommand>
                """.formatted(start, end);

        mockMvc.perform(post("/xml_api/command")
                        .contentType(MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
