package com.example.gateway.api.controller;

import jakarta.ws.rs.core.Response;
import org.springframework.http.ResponseEntity;

public final class ResponseEntityAdapter {

    private ResponseEntityAdapter() {
    }

    public static <T> ResponseEntity<T> from(Response response) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatus());
        response.getHeaders().forEach((name, values) -> values.forEach(value -> builder.header(name, value)));
        @SuppressWarnings("unchecked")
        T entity = (T) response.getEntity();
        return builder.body(entity);
    }
}
