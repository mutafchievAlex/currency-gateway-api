package com.example.gateway.api.error.mapper;

import com.example.gateway.api.error.ApiErrorResponse;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ThrowableExceptionMapper implements ApiExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return true;
    }

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("Unexpected exception caught", exception);
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        payload.setType("Internal Server Error");
        payload.setMessage(exception != null && exception.getMessage() != null
                ? exception.getMessage()
                : Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(payload)
                .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                .build();
    }
}
