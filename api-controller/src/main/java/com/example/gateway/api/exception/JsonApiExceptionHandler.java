package com.example.gateway.api.exception;

import com.example.gateway.api.exception.mapper.ApiExceptionMapper;
import com.example.gateway.api.json.generated.model.ApiErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.gateway.api.controller.json")
public class JsonApiExceptionHandler extends AbstractApiExceptionHandler {

    public JsonApiExceptionHandler(List<ApiExceptionMapper<?>> mappers) {
        super(mappers, JsonApiExceptionHandler.class);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handle(Throwable exception) {
        ApiExceptionMapper.ErrorResponse error = resolve(exception);
        ApiErrorResponse payload = new ApiErrorResponse();
        payload.setCode(error.status().value());
        payload.setType(error.type());
        payload.setMessage(error.message());
        return ResponseEntity.status(error.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload);
    }
}
