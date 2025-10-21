package com.example.gateway.api.error.mapper;

import com.example.gateway.api.error.ApiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
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
    public Response toResponse(Throwable exception) {
        ConstraintViolationException violationException = (ConstraintViolationException) exception;
        LOGGER.info("Constraint violation: {}", violationException.getMessage());

        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(Response.Status.BAD_REQUEST.getStatusCode());
        payload.setType("Validation failed");
        payload.setMessage(buildMessage(violationException));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(payload)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
    }

    private String buildMessage(ConstraintViolationException violationException) {
        return violationException.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
