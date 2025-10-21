package com.example.gateway.api.exception;

import com.example.gateway.api.exception.mapper.ApiExceptionMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "com.example.gateway.api.controller.xml")
public class XmlApiExceptionHandler extends AbstractApiExceptionHandler {

    public XmlApiExceptionHandler(List<ApiExceptionMapper<?>> mappers) {
        super(mappers, XmlApiExceptionHandler.class);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handle(Throwable exception) {
        ApiExceptionMapper.ErrorResponse error = resolve(exception);
        com.example.gateway.api.xml.generated.model.ApiErrorResponse payload =
                new com.example.gateway.api.xml.generated.model.ApiErrorResponse();
        payload.setCode(error.status().value());
        payload.setType(error.type());
        payload.setMessage(error.message());
        return ResponseEntity.status(error.status())
                .contentType(MediaType.APPLICATION_XML)
                .body(payload);
    }
}
