package com.example.gateway.api.xml;

import com.example.gateway.api.config.JaxRsResponseConfigurer;
import com.example.gateway.api.config.JaxRsResponseReturnValueHandler;
import com.example.gateway.api.support.ExchangeRateTestFixtures;
import com.example.gateway.api.xml.mapper.XmlApiMapper;
import com.example.gateway.application.exception.ExchangeRateNotFoundException;
import com.example.gateway.application.ExchangeRateService;
import com.example.gateway.application.RequestLogService;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.common.exception.MissingRequiredValueException;
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

import jakarta.validation.Validator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(XmlExchangeRateController.class)
@Import({XmlExchangeRateControllerTest.MapperConfig.class,
        JaxRsResponseReturnValueHandler.class,
        JaxRsResponseConfigurer.class})
class XmlExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @MockBean
    private RequestLogService requestLogService;

    @Test
    void returnsCurrentRateAsXml() throws Exception {
        ExchangeRate rate = ExchangeRateTestFixtures.rate().build();
        when(exchangeRateService.getLatest("USD", "EUR")).thenReturn(rate);

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/exchangeRate/baseCurrency").string("USD"))
                .andExpect(xpath("/exchangeRate/targetCurrency").string("EUR"))
                .andExpect(xpath("/exchangeRate/rate").string("0.9200"))
                .andExpect(xpath("/exchangeRate/timestamp").string(ExchangeRateTestFixtures.TIMESTAMP.toString()));

        ArgumentCaptor<RequestLog> captor = ArgumentCaptor.forClass(RequestLog.class);
        verify(requestLogService).record(captor.capture());
        assertThat(captor.getValue().requestId()).isEqualTo("req-123");
        assertThat(captor.getValue().endpoint()).isEqualTo("/api/exchange-rates/current");
    }

    @Test
    void returnsNotFoundWhenCurrentRateMissing() throws Exception {
        when(exchangeRateService.getLatest("USD", "EUR"))
                .thenThrow(new ExchangeRateNotFoundException("USD", "EUR"));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound())
                .andExpect(xpath("/error/code").string(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .andExpect(xpath("/error/type").string(HttpStatus.NOT_FOUND.getReasonPhrase()));
    }

    @Test
    void returnsConflictWhenRequestDuplicated() throws Exception {
        doThrow(new DuplicateRequestException("duplicate"))
                .when(requestLogService).record(any(RequestLog.class));

        mockMvc.perform(get("/api/exchange-rates/current")
                        .queryParam("requestId", "req-123")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "eur")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isConflict())
                .andExpect(xpath("/error/type").string("Duplicate request"))
                .andExpect(xpath("/error/code").string(String.valueOf(HttpStatus.CONFLICT.value())));
    }

    @Test
    void returnsBadRequestWhenMandatoryValueMissing() throws Exception {
        when(exchangeRateService.getLatest("USD", "EUR"))
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
        when(exchangeRateService.findHistory("USD", "JPY", scenario.start(), scenario.end())).thenReturn(scenario.rates());

        mockMvc.perform(get("/api/exchange-rates/history")
                        .queryParam("requestId", "req-456")
                        .queryParam("baseCurrency", "usd")
                        .queryParam("targetCurrency", "jpy")
                        .queryParam("start", scenario.start().toString())
                        .queryParam("end", scenario.end().toString())
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(xpath("/exchangeRateHistory/rates/rate[1]/targetCurrency").string("JPY"))
                .andExpect(xpath("/exchangeRateHistory/rates/rate[2]/rate").string("111.00"));

        verify(requestLogService, Mockito.atLeastOnce()).record(any(RequestLog.class));
    }

    @Test
    void rejectsInvalidHistoryRange() throws Exception {
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

    @TestConfiguration
    static class MapperConfig {

        @Bean
        XmlApiMapper xmlApiMapper() {
            return Mappers.getMapper(XmlApiMapper.class);
        }

        @Bean
        BeanValidationService beanValidationService(Validator validator) {
            return new BeanValidationService(validator);
        }
    }
}
