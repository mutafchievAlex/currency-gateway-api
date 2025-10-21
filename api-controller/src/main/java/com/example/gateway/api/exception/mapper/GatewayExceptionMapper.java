package com.example.gateway.api.exception.mapper;

import com.example.gateway.common.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GatewayExceptionMapper implements ApiExceptionMapper<GatewayException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return exception instanceof GatewayException;
    }

    @Override
    public ErrorResponse toErrorResponse(Throwable exception) {
        GatewayException gatewayException = (GatewayException) exception;
        LOGGER.info("Handled gateway exception: {}", gatewayException.getClass().getSimpleName());
        HttpStatus status = HttpStatus.resolve(gatewayException.getStatusCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ErrorResponse(status, gatewayException.getType(), gatewayException.getMessage());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
