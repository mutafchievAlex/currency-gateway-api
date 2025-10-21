package com.example.gateway.domain.exception;

/**
 * Indicates that a request failed general validation rules.
 */
public class RequestValidationException extends GatewayValidationException {

    private static final String DEFAULT_TYPE = "Validation failed";

    public RequestValidationException(String message) {
        super(message, DEFAULT_TYPE);
    }

    public RequestValidationException(String message, Throwable cause) {
        super(message, cause, DEFAULT_TYPE);
    }
}
