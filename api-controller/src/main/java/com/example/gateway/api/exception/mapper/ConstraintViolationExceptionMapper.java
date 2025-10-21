package com.example.gateway.api.exception.mapper;

import com.example.gateway.api.exception.ApiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiErrorResponse> toResponse(Throwable exception) {
        ConstraintViolationException violationException = (ConstraintViolationException) exception;
        LOGGER.info("Constraint violation: {}", violationException.getMessage());
        String message = buildMessage(violationException);
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(HttpStatus.BAD_REQUEST.value());
        payload.setType("Validation failed");
        payload.setMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
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
