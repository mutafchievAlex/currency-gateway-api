package com.example.gateway.api.exception.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ConstraintViolationExceptionMapper implements ApiExceptionMapper<ConstraintViolationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return exception instanceof ConstraintViolationException;
    }

    @Override
    public ErrorResponse toErrorResponse(Throwable exception) {
        ConstraintViolationException violationException = (ConstraintViolationException) exception;
        LOGGER.info("Constraint violation: {}", violationException.getMessage());
        String message = buildMessage(violationException);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", message);
    }

    private String buildMessage(ConstraintViolationException violationException) {
        return violationException.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .filter(msg -> msg != null && !msg.isBlank())
                .collect(Collectors.joining(", "));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
