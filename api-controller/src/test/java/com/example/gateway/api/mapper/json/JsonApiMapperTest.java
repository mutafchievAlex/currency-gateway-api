package com.example.gateway.api.mapper.json;

import com.example.gateway.api.json.generated.model.JsonQuote;
import com.example.gateway.domain.model.ExchangeRate;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JsonApiMapperTest {

    private final JsonApiMapper mapper = Mappers.getMapper(JsonApiMapper.class);

    @Test
    void mapsExchangeRateTimestampToOffsetDateTime() {
        Instant timestamp = Instant.parse("2024-03-20T10:15:30Z");
        ExchangeRate rate = new ExchangeRate("USD", "EUR", new BigDecimal("0.92"), timestamp);

        JsonQuote quote = mapper.toQuote(rate);

        assertThat(quote.getTimestamp()).isEqualTo(OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC));
        assertThat(quote.getRate()).isEqualTo("0.92");
        assertThat(quote.getProvider()).isEqualTo(JsonQuote.ProviderEnum.FIXER);
    }

    @Test
    void mapsCollectionOfQuotes() {
        Instant now = Instant.parse("2024-03-20T10:15:30Z");
        List<ExchangeRate> rates = List.of(
                new ExchangeRate("USD", "EUR", new BigDecimal("0.91"), now.minusSeconds(60)),
                new ExchangeRate("USD", "EUR", new BigDecimal("0.92"), now)
        );

        List<JsonQuote> quotes = mapper.toQuotes(rates);

        assertThat(quotes).hasSize(2);
        assertThat(quotes.get(0).getTimestamp()).isEqualTo(OffsetDateTime.ofInstant(now.minusSeconds(60), ZoneOffset.UTC));
        assertThat(quotes.get(1).getRate()).isEqualTo("0.92");
    }
}
