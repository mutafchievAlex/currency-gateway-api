package com.example.gateway.domain.service;

import java.util.Collection;

/**
 * Service responsible for coordinating the retrieval and persistence of external rate data.
 */
public interface RatesCollectorService {

    void collectLatestRates(String baseCurrency, Collection<String> targetCurrencies);
}
