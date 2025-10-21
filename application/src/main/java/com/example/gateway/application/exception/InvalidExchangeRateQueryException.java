package com.example.gateway.application.exception;

import com.example.gateway.common.exception.RequestValidationException;

/**
 * Raised when an exchange rate query is malformed or violates business rules.
 */
public class InvalidExchangeRateQueryException extends RequestValidationException {

    public InvalidExchangeRateQueryException(String message) {
        super(message);
    }
}
