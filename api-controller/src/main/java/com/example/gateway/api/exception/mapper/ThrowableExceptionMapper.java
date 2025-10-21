package com.example.gateway.api.exception.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ThrowableExceptionMapper implements ApiExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return true;
    }

    @Override
    public ErrorResponse toErrorResponse(Throwable exception) {
        LOGGER.error("Unexpected exception caught", exception);
        String message = exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()
                ? exception.getMessage()
                : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
}
