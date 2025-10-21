package com.example.gateway.api.exception.mapper;

import com.example.gateway.api.exception.ApiErrorResponse;
import com.example.gateway.domain.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GatewayExceptionMapper implements ApiExceptionMapper<GatewayException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return exception instanceof GatewayException;
    }

    @Override
    public ResponseEntity<ApiErrorResponse> toResponse(Throwable exception) {
        GatewayException gatewayException = (GatewayException) exception;
        LOGGER.info("Handled gateway exception: {}", gatewayException.getClass().getSimpleName());
        HttpStatus status = HttpStatus.resolve(gatewayException.getStatusCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(status.value());
        payload.setType(gatewayException.getType());
        payload.setMessage(gatewayException.getMessage());
        return ResponseEntity.status(status).body(payload);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
