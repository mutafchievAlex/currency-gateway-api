package com.example.gateway.api.json.generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class ExchangeRateResponse {

    @JsonProperty("baseCurrency")
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    private String baseCurrency;

    @JsonProperty("targetCurrency")
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    private String targetCurrency;

    @JsonProperty("rate")
    @NotNull
    private BigDecimal rate;

    @JsonProperty("timestamp")
    @NotNull
    private Instant timestamp;

    public ExchangeRateResponse baseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }

    public ExchangeRateResponse targetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
        return this;
    }

    public ExchangeRateResponse rate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public ExchangeRateResponse timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExchangeRateResponse that)) {
            return false;
        }
        return Objects.equals(baseCurrency, that.baseCurrency)
                && Objects.equals(targetCurrency, that.targetCurrency)
                && Objects.equals(rate, that.rate)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, targetCurrency, rate, timestamp);
    }

    @Override
    public String toString() {
        return "ExchangeRateResponse{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", rate=" + rate +
                ", timestamp=" + timestamp +
                '}';
    }
}
