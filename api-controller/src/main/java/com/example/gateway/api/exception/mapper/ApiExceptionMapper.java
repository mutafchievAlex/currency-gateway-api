package com.example.gateway.api.exception.mapper;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

public interface ApiExceptionMapper<T extends Throwable> extends Ordered {

    boolean supports(Throwable exception);

    ErrorResponse toErrorResponse(Throwable exception);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    record ErrorResponse(HttpStatus status, String type, String message) {
        public ErrorResponse {
            status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
            type = (type == null || type.isBlank()) ? status.getReasonPhrase() : type;
            message = (message == null || message.isBlank()) ? status.getReasonPhrase() : message;
        }
    }
}
