package com.example.gateway.api.support;

import com.example.gateway.domain.ExchangeRate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class ExchangeRateTestFixtures {

    public static final Instant TIMESTAMP = Instant.parse("2024-03-15T10:15:30Z");

    private ExchangeRateTestFixtures() {
    }

    public static ExchangeRateBuilder rate() {
        return new ExchangeRateBuilder();
    }

    public static HistoryScenario usdToJpyHistoryScenario() {
        Instant end = TIMESTAMP;
        Instant start = end.minus(3, ChronoUnit.HOURS);
        List<ExchangeRate> rates = List.of(
                rate().withTargetCurrency("JPY")
                        .withRate(new BigDecimal("110.00"))
                        .withTimestamp(end.minus(2, ChronoUnit.HOURS))
                        .build(),
                rate().withTargetCurrency("JPY")
                        .withRate(new BigDecimal("111.00"))
                        .withTimestamp(end.minus(1, ChronoUnit.HOURS))
                        .build()
        );
        return new HistoryScenario(start, end, rates);
    }

    public record HistoryScenario(Instant start, Instant end, List<ExchangeRate> rates) {
    }

    public static final class ExchangeRateBuilder {
        private String baseCurrency = "USD";
        private String targetCurrency = "EUR";
        private BigDecimal rate = new BigDecimal("0.9200");
        private Instant timestamp = TIMESTAMP;

        private ExchangeRateBuilder() {
        }

        public ExchangeRateBuilder withBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
            return this;
        }

        public ExchangeRateBuilder withTargetCurrency(String targetCurrency) {
            this.targetCurrency = targetCurrency;
            return this;
        }

        public ExchangeRateBuilder withRate(BigDecimal rate) {
            this.rate = rate;
            return this;
        }

        public ExchangeRateBuilder withTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ExchangeRate build() {
            return new ExchangeRate(baseCurrency, targetCurrency, rate, timestamp);
        }
    }
}
