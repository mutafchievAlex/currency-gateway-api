package com.example.gateway.api.error.mapper;

import jakarta.ws.rs.core.Response;
import org.springframework.core.Ordered;

public interface ApiExceptionMapper<T extends Throwable> extends Ordered {

    boolean supports(Throwable exception);

    Response toResponse(Throwable exception);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
