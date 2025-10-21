package com.example.gateway.api.mapper.xml;

import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.model.ExchangeRate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XmlApiMapperTest {

    private final XmlApiMapper mapper = Mappers.getMapper(XmlApiMapper.class);

    @Test
    void mapsExchangeRateTimestampToOffsetDateTime() {
        Instant timestamp = Instant.parse("2024-03-20T10:15:30Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.92"), timestamp);

        ExchangeRateResponse response = mapper.toExchangeRateResponse(rate);

        assertThat(response.getTimestamp()).isEqualTo(OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC));
        assertThat(response.getBaseCurrency()).isEqualTo("USD");
        assertThat(response.getTargetCurrency()).isEqualTo("EUR");
    }

    @Test
    void mapsHistoryResponseWithConvertedRates() {
        Instant now = Instant.parse("2024-03-20T10:15:30Z");
        List<ExchangeRate> rates = List.of(
                new ExchangeRate("USD", "EUR", new BigDecimal("0.91"), now.minusSeconds(60)),
                new ExchangeRate("USD", "EUR", new BigDecimal("0.92"), now)
        );

        ExchangeRateHistoryResponse response = mapper.toHistoryResponse(rates);

        assertThat(response.getRates()).hasSize(2);
        assertThat(response.getRates().get(0).getTimestamp()).isEqualTo(OffsetDateTime.ofInstant(now.minusSeconds(60), ZoneOffset.UTC));
        assertThat(response.getRates().get(1).getRate()).isEqualTo("0.92");
    }
}
