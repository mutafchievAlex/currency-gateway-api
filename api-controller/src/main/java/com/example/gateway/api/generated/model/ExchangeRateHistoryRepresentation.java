package com.example.gateway.api.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@JacksonXmlRootElement(localName = "exchangeRateHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeRateHistoryRepresentation {

    @JsonProperty("rates")
    @JacksonXmlElementWrapper(localName = "rates")
    @JacksonXmlProperty(localName = "rate")
    @NotNull
    @Valid
    private List<ExchangeRateRepresentation> rates = new ArrayList<>();

    public ExchangeRateHistoryRepresentation rates(List<ExchangeRateRepresentation> rates) {
        this.rates = rates;
        return this;
    }

    public ExchangeRateHistoryRepresentation addRatesItem(ExchangeRateRepresentation rate) {
        this.rates.add(rate);
        return this;
    }

    public List<ExchangeRateRepresentation> getRates() {
        return rates;
    }

    public void setRates(List<ExchangeRateRepresentation> rates) {
        this.rates = rates == null ? new ArrayList<>() : rates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExchangeRateHistoryRepresentation that)) {
            return false;
        }
        return Objects.equals(rates, that.rates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rates);
    }

    @Override
    public String toString() {
        return "ExchangeRateHistoryRepresentation{" +
                "rates=" + rates +
                '}';
    }
}
