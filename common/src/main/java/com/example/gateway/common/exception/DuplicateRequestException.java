package com.example.gateway.common.exception;

public class DuplicateRequestException extends GatewayClientException {

    private static final int CONFLICT = 409;
    private static final String TYPE = "Duplicate request";

    public DuplicateRequestException(String message) {
        super(message, CONFLICT, TYPE);
    }

    public DuplicateRequestException(String message, Throwable cause) {
        super(message, cause, CONFLICT, TYPE);
    }
}
