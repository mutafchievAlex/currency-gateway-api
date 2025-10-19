package com.example.gateway.api.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@JacksonXmlRootElement(localName = "exchangeRate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateRepresentation {

    @JsonProperty("baseCurrency")
    @JacksonXmlProperty(localName = "baseCurrency")
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    private String baseCurrency;

    @JsonProperty("targetCurrency")
    @JacksonXmlProperty(localName = "targetCurrency")
    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    private String targetCurrency;

    @JsonProperty("rate")
    @JacksonXmlProperty(localName = "rate")
    @NotNull
    private BigDecimal rate;

    @JsonProperty("timestamp")
    @JacksonXmlProperty(localName = "timestamp")
    @NotNull
    private Instant timestamp;

    public ExchangeRateRepresentation baseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }

    public ExchangeRateRepresentation targetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
        return this;
    }

    public ExchangeRateRepresentation rate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public ExchangeRateRepresentation timestamp(Instant timestamp) {
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
        if (!(o instanceof ExchangeRateRepresentation that)) {
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
        return "ExchangeRateRepresentation{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", rate=" + rate +
                ", timestamp=" + timestamp +
                '}';
    }
}
