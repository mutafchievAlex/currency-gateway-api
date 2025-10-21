package com.example.gateway.api.exception.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

@Component
public class MethodArgumentNotValidExceptionMapper implements ApiExceptionMapper<MethodArgumentNotValidException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodArgumentNotValidExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return exception instanceof MethodArgumentNotValidException;
    }

    @Override
    public ErrorResponse toErrorResponse(Throwable exception) {
        MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) exception;
        LOGGER.info("Request body validation failed: {}", notValidException.getMessage());
        String message = buildMessage(notValidException);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", message);
    }

    private String buildMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(message -> message != null && !message.isBlank())
                .collect(Collectors.joining(", "));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 30;
    }
}
