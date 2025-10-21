package com.example.gateway.common.exception;

/**
 * Base type for validation related problems that should surface as HTTP 400 responses.
 */
public abstract class GatewayValidationException extends GatewayClientException {

    private static final int BAD_REQUEST = 400;

    protected GatewayValidationException(String message) {
        this(message, "Bad Request");
    }

    protected GatewayValidationException(String message, String type) {
        super(message, BAD_REQUEST, type);
    }

    protected GatewayValidationException(String message, Throwable cause, String type) {
        super(message, cause, BAD_REQUEST, type);
    }
}
