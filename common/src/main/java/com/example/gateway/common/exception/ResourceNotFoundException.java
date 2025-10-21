package com.example.gateway.common.exception;

/**
 * Signals that a requested resource could not be located.
 */
public class ResourceNotFoundException extends GatewayClientException {

    private static final int NOT_FOUND = 404;
    private static final String DEFAULT_TYPE = "Not Found";

    public ResourceNotFoundException(String message) {
        super(message, NOT_FOUND, DEFAULT_TYPE);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, NOT_FOUND, DEFAULT_TYPE);
    }
}
