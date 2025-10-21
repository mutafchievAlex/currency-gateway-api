package com.example.gateway.domain.exception;

/**
 * Raised when an exchange rate query is malformed or violates business rules.
 */
public class InvalidExchangeRateQueryException extends RequestValidationException {

    public InvalidExchangeRateQueryException(String message) {
        super(message);
    }
}
