package com.example.gateway.common.exception;

/**
 * Marker base class for 4xx HTTP exceptions thrown by the gateway.
 */
public abstract class GatewayClientException extends GatewayException {

    protected GatewayClientException(String message, int statusCode, String type) {
        super(message, statusCode, type);
    }

    protected GatewayClientException(String message, Throwable cause, int statusCode, String type) {
        super(message, cause, statusCode, type);
    }
}
