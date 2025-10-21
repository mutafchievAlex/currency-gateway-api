package com.example.gateway.api.exception;

import com.example.gateway.api.exception.mapper.ApiExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice(basePackages = "com.example.gateway.api")
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final List<ApiExceptionMapper<?>> mappers;

    public ApiExceptionHandler(List<ApiExceptionMapper<?>> mappers) {
        this.mappers = mappers.stream()
                .sorted(Comparator.comparingInt(ApiExceptionMapper::getOrder))
                .toList();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorResponse> handle(Throwable exception) {
        return mappers.stream()
                .filter(mapper -> mapper.supports(exception))
                .findFirst()
                .map(mapper -> mapper.toResponse(exception))
                .orElseGet(() -> defaultResponse(exception));
    }

    private ResponseEntity<ApiErrorResponse> defaultResponse(Throwable exception) {
        if (exception != null) {
            LOGGER.error("Unexpected exception caught", exception);
        }
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        payload.setType(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        payload.setMessage(exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()
                ? exception.getMessage()
                : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(payload);
    }
}
