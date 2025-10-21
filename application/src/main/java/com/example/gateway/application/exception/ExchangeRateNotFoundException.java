package com.example.gateway.application.exception;

import com.example.gateway.common.exception.ResourceNotFoundException;

/**
 * Thrown when an exchange rate cannot be located for the requested currency pair.
 */
public class ExchangeRateNotFoundException extends ResourceNotFoundException {

    public ExchangeRateNotFoundException(String baseCurrency, String targetCurrency) {
        super("No exchange rate found for %s/%s".formatted(baseCurrency, targetCurrency));
    }
}
