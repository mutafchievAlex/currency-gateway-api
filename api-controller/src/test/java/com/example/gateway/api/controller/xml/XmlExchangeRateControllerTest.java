package com.example.gateway.api.controller.xml;

import com.example.gateway.api.controller.json.JsonExchangeRateController;
import com.example.gateway.api.mapper.xml.XmlApiMapper;
import com.example.gateway.api.support.ExchangeRateTestFixtures;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.exception.ExchangeRateNotFoundException;
import com.example.gateway.domain.exception.InvalidExchangeRateQueryException;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.service.ExchangeRateQueryService;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(XmlExchangeRateController.class)
@Import(XmlExchangeRateControllerTest.class)
@ContextConfiguration(classes = {XmlExchangeRateController.class})
class XmlExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateQueryService exchangeRateQueryService;

    @MockBean
    private XmlApiMapper xmlApiMapper;

    @Test
    void returnsCurrentRateAsXml() throws Exception {
        ExchangeRate rate = ExchangeRateTestFixtures.rate().build();
        ExchangeRateResponse response = new ExchangeRateResponse()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate("0.9200")
                .timestamp(OffsetDateTime.ofInstant(ExchangeRateTestFixtures.TIMESTAMP, ZoneOffset.UTC));
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenReturn(rate);
        when(xmlApiMapper.toExchangeRateResponse(rate)).thenReturn(response);

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Location", "/api/exchange-rates/current"))
                .andExpect(xpath("/exchangeRate/baseCurrency").string("USD"))
                .andExpect(xpath("/exchangeRate/targetCurrency").string("EUR"))
                .andExpect(xpath("/exchangeRate/rate").string("0.9200"))
                .andExpect(xpath("/exchangeRate/timestamp").string(ExchangeRateTestFixtures.TIMESTAMP.toString()));
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                .thenThrow(new ExchangeRateNotFoundException("USD", "EUR"));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound());
        }

        @Test
        void returnsBadRequestWhenMandatoryValueMissing() throws Exception {
            when(exchangeRateQueryService.getCurrentRate("req-123", "/api/exchange-rates/current", "usd", "eur"))
                    .thenThrow(new MissingRequiredValueException("baseCurrency must not be null"));

            mockMvc.perform(get("/api/exchange-rates/current")
                            .queryParam("requestId", "req-123")
                            .queryParam("baseCurrency", "usd")
                            .queryParam("targetCurrency", "eur")
                            .accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isBadRequest())
                    .andExpect(xpath("/error/type").string("Missing required value"))
                    .andExpect(xpath("/error/code").string(String.valueOf(HttpStatus.BAD_REQUEST.value())));
        }

        @Test
        void returnsHistoryAsXml() throws Exception {
            ExchangeRateTestFixtures.HistoryScenario scenario = ExchangeRateTestFixtures.usdToJpyHistoryScenario();
            ExchangeRateResponse firstRate = new ExchangeRateResponse()
                    .baseCurrency("USD")
                    .targetCurrency("JPY")
                    .rate("110.00")
                    .timestamp(OffsetDateTime.ofInstant(scenario.start().plus(1, java.time.temporal.ChronoUnit.HOURS), ZoneOffset.UTC));
            ExchangeRateResponse secondRate = new ExchangeRateResponse()
                    .baseCurrency("USD")
                    .targetCurrency("JPY")
                    .rate("111.00")
                    .timestamp(OffsetDateTime.ofInstant(scenario.end().minus(1, java.time.temporal.ChronoUnit.HOURS), ZoneOffset.UTC));
            ExchangeRateHistoryResponse historyResponse = new ExchangeRateHistoryResponse()
                    .rates(List.of(firstRate, secondRate));
            when(exchangeRateQueryService.getHistory("req-456", "/api/exchange-rates/history", "usd", "jpy",
                    scenario.start().atOffset(java.time.ZoneOffset.UTC),
                    scenario.end().atOffset(java.time.ZoneOffset.UTC))).thenReturn(scenario.rates());
            when(xmlApiMapper.toHistoryResponse(anyList())).thenReturn(historyResponse);

            mockMvc.perform(get("/api/exchange-rates/history")
                            .queryParam("requestId", "req-456")
                            .queryParam("baseCurrency", "usd")
                            .queryParam("targetCurrency", "jpy")
                            .queryParam("start", scenario.start().toString())
                            .queryParam("end", scenario.end().toString())
                            .accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Location", "/api/exchange-rates/history"))
                    .andExpect(xpath("/exchangeRateHistory/rates/rate[1]/targetCurrency").string("JPY"))
                    .andExpect(xpath("/exchangeRateHistory/rates/rate[2]/rate").string("111.00"));
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
                            .accept(MediaType.APPLICATION_XML))
                    .andExpect(status().isBadRequest())
                    .andExpect(xpath("/error/code").string(String.valueOf(HttpStatus.BAD_REQUEST.value())));
        }
}
