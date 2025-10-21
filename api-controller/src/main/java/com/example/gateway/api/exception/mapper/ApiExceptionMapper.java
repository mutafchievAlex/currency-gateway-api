package com.example.gateway.api.exception.mapper;

import com.example.gateway.api.exception.ApiErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;

public interface ApiExceptionMapper<T extends Throwable> extends Ordered {

    boolean supports(Throwable exception);

    ResponseEntity<ApiErrorResponse> toResponse(Throwable exception);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
