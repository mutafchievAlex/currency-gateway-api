package com.example.gateway.api.exception;

import com.example.gateway.api.exception.mapper.ApiExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;

abstract class AbstractApiExceptionHandler {

    private final List<ApiExceptionMapper<?>> mappers;
    private final Logger logger;

    protected AbstractApiExceptionHandler(List<ApiExceptionMapper<?>> mappers, Class<?> loggerType) {
        this.mappers = mappers.stream()
                .sorted(Comparator.comparingInt(ApiExceptionMapper::getOrder))
                .toList();
        this.logger = LoggerFactory.getLogger(loggerType);
    }

    protected ApiExceptionMapper.ErrorResponse resolve(Throwable exception) {
        return mappers.stream()
                .filter(mapper -> mapper.supports(exception))
                .findFirst()
                .map(mapper -> mapper.toErrorResponse(exception))
                .orElseGet(() -> defaultError(exception));
    }

    private ApiExceptionMapper.ErrorResponse defaultError(Throwable exception) {
        if (exception != null) {
            logger.error("Unexpected exception caught", exception);
        }
        String message = (exception != null && exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        return new ApiExceptionMapper.ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
}
