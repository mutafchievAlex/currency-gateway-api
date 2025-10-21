package com.example.gateway.api.error;

import com.example.gateway.api.error.mapper.ApiExceptionMapper;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice(basePackages = "com.example.gateway.api")
public class ApiExceptionHandler {

    private final List<ApiExceptionMapper<?>> mappers;

    public ApiExceptionHandler(List<ApiExceptionMapper<?>> mappers) {
        this.mappers = mappers.stream()
                .sorted(Comparator.comparingInt(ApiExceptionMapper::getOrder))
                .toList();
    }

    @ExceptionHandler(Throwable.class)
    public Response handle(Throwable exception) {
        return mappers.stream()
                .filter(mapper -> mapper.supports(exception))
                .findFirst()
                .map(mapper -> mapper.toResponse(exception))
                .orElseGet(() -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(defaultError(exception))
                        .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                        .build());
    }

    private ApiErrorResponse defaultError(Throwable exception) {
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        payload.setType("Internal Server Error");
        payload.setMessage(exception != null && exception.getMessage() != null
                ? exception.getMessage()
                : Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return payload;
    }
}
