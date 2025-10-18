package com.example.gateway.application.port;

import java.util.Collection;

/**
 * Abstraction over external providers that expose exchange rate information.
 */
public interface ExternalRatesClient {

    RatesSnapshot fetchLatestRates(String baseCurrency, Collection<String> targetCurrencies);
}
