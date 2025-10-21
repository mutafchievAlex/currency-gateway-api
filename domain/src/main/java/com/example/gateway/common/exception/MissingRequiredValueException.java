package com.example.gateway.common.exception;

/**
 * Indicates that a mandatory value was not provided when required by the system.
 */
public class MissingRequiredValueException extends GatewayValidationException {

    private static final String FIELD_TEMPLATE = "%s must not be null";
    private static final String DEFAULT_MESSAGE = "Required value must not be null";
    private static final String TYPE = "Missing required value";

    public static MissingRequiredValueException forField(String fieldName) {
        String message = fieldName == null || fieldName.isBlank() ? DEFAULT_MESSAGE : FIELD_TEMPLATE.formatted(fieldName);
        return new MissingRequiredValueException(message);
    }

    public MissingRequiredValueException(String message) {
        super(message == null || message.isBlank() ? DEFAULT_MESSAGE : message, TYPE);
    }

    public MissingRequiredValueException(String message, Throwable cause) {
        super(message == null || message.isBlank() ? DEFAULT_MESSAGE : message, cause, TYPE);
    }
}
