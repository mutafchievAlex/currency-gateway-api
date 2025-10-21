package com.example.gateway.domain.repository;

import java.util.Collection;

/**
 * Abstraction over external providers that expose exchange rate information.
 */
public interface ExternalRatesClient {

    RatesSnapshot fetchLatestRates(String baseCurrency, Collection<String> targetCurrencies);
}
