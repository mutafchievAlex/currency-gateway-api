package com.example.gateway.common.exception;

/**
 * Base type for all custom gateway runtime exceptions that carry HTTP semantics.
 */
public abstract class GatewayException extends RuntimeException {

    private final int statusCode;
    private final String type;

    protected GatewayException(String message, int statusCode, String type) {
        super(resolveMessage(message, type));
        this.statusCode = statusCode;
        this.type = resolveType(type);
    }

    protected GatewayException(String message, Throwable cause, int statusCode, String type) {
        super(resolveMessage(message, type), cause);
        this.statusCode = statusCode;
        this.type = resolveType(type);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getType() {
        return type;
    }

    private static String resolveMessage(String message, String type) {
        if (message != null && !message.isBlank()) {
            return message;
        }
        String fallbackType = resolveType(type);
        return fallbackType != null ? fallbackType : "Unexpected error";
    }

    private static String resolveType(String type) {
        if (type == null || type.isBlank()) {
            return "Error";
        }
        return type;
    }
}
