package com.example.gateway.api.support;

import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public final class JaxRsResponseEntityAdapter {

    private JaxRsResponseEntityAdapter() {
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<T> toResponseEntity(Response response) {
        if (response == null) {
            return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        response.getHeaders().forEach((name, values) -> values.forEach(value -> headers.add(name, String.valueOf(value))));

        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatus()).headers(headers);

        if (response.getMediaType() != null) {
            MediaType mediaType = MediaType.parseMediaType(response.getMediaType().toString());
            builder.contentType(mediaType);
        }

        if (!response.hasEntity()) {
            return (ResponseEntity<T>) builder.build();
        }

        Object entity = response.getEntity();
        return builder.body((T) entity);
    }
}
