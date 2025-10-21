package com.example.gateway.api.error.mapper;

import com.example.gateway.api.error.ApiErrorResponse;
import com.example.gateway.common.exception.GatewayException;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class GatewayExceptionMapper implements ApiExceptionMapper<GatewayException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return exception instanceof GatewayException;
    }

    @Override
    public Response toResponse(Throwable exception) {
        GatewayException gatewayException = (GatewayException) exception;
        LOGGER.info("Handled gateway exception: {}", gatewayException.getClass().getSimpleName());

        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(gatewayException.getStatusCode());
        payload.setType(gatewayException.getType());
        payload.setMessage(gatewayException.getMessage());

        return Response.status(gatewayException.getStatusCode())
                .entity(payload)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
