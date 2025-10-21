package com.example.gateway.api.exception;

import com.example.gateway.api.json.generated.model.ApiErrorResponse;
import com.example.gateway.api.exception.mapper.ApiExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice(basePackages = "com.example.gateway.api")
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final List<ApiExceptionMapper<?>> mappers;

    public ApiExceptionHandler(List<ApiExceptionMapper<?>> mappers) {
        this.mappers = mappers.stream()
                .sorted(Comparator.comparingInt(ApiExceptionMapper::getOrder))
                .toList();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handle(Throwable exception, NativeWebRequest request) {
        ApiExceptionMapper.ErrorResponse error = mappers.stream()
                .filter(mapper -> mapper.supports(exception))
                .findFirst()
                .map(mapper -> mapper.toErrorResponse(exception))
                .orElseGet(() -> defaultError(exception));

        MediaType mediaType = resolveMediaType(request);
        if (MediaType.APPLICATION_XML.isCompatibleWith(mediaType)) {
            com.example.gateway.api.xml.generated.model.ApiErrorResponse payload =
                    new com.example.gateway.api.xml.generated.model.ApiErrorResponse();
            payload.setCode(error.status().value());
            payload.setType(error.type());
            payload.setMessage(error.message());
            return ResponseEntity.status(error.status())
                    .contentType(MediaType.APPLICATION_XML)
                    .body(payload);
        }

        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(error.status().value());
        payload.setType(error.type());
        payload.setMessage(error.message());
        return ResponseEntity.status(error.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload);
    }

    private ApiExceptionMapper.ErrorResponse defaultError(Throwable exception) {
        if (exception != null) {
            LOGGER.error("Unexpected exception caught", exception);
        }
        return new ApiExceptionMapper.ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                exception != null && exception.getMessage() != null && !exception.getMessage().isBlank()
                        ? exception.getMessage()
                        : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private MediaType resolveMediaType(NativeWebRequest request) {
        String[] accepts = request.getHeaderValues(HttpHeaders.ACCEPT);
        if (accepts == null || accepts.length == 0) {
            return MediaType.APPLICATION_JSON;
        }

        List<MediaType> candidates = new ArrayList<>();
        for (String accept : accepts) {
            try {
                candidates.addAll(MediaType.parseMediaTypes(accept));
            } catch (IllegalArgumentException ex) {
                LOGGER.debug("Ignoring invalid media type '{}': {}", accept, ex.getMessage());
            }
        }
        if (candidates.isEmpty()) {
            return MediaType.APPLICATION_JSON;
        }

        MediaType.sortBySpecificityAndQuality(candidates);
        for (MediaType candidate : candidates) {
            if (MediaType.APPLICATION_XML.isCompatibleWith(candidate)) {
                return MediaType.APPLICATION_XML;
            }
            if (MediaType.APPLICATION_JSON.isCompatibleWith(candidate)) {
                return MediaType.APPLICATION_JSON;
            }
        }
        return MediaType.APPLICATION_JSON;
    }
}
