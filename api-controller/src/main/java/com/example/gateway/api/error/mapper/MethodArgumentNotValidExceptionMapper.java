package com.example.gateway.api.error.mapper;

import com.example.gateway.api.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
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
    public Response toResponse(Throwable exception) {
        MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) exception;
        LOGGER.info("Request body validation failed: {}", notValidException.getMessage());

        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(Response.Status.BAD_REQUEST.getStatusCode());
        payload.setType("Validation failed");
        payload.setMessage(buildMessage(notValidException));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(payload)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
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
