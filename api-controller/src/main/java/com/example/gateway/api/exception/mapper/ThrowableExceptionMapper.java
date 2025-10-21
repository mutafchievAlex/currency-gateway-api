package com.example.gateway.api.exception.mapper;

import com.example.gateway.api.exception.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ThrowableExceptionMapper implements ApiExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThrowableExceptionMapper.class);

    @Override
    public boolean supports(Throwable exception) {
        return true;
    }

    @Override
    public ResponseEntity<ApiErrorResponse> toResponse(Throwable exception) {
        LOGGER.error("Unexpected exception caught", exception);
        String message = exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()
                ? exception.getMessage()
                : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        payload.setType("Internal Server Error");
        payload.setMessage(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }
}
