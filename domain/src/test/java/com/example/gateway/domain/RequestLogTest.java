package com.example.gateway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLogTest {

    @Test
    @DisplayName("constructor stores all values")
    void constructorStoresValues() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");
        RequestLog log = new RequestLog("id-1", "/rates", "GET", timestamp);

        assertEquals("id-1", log.requestId());
        assertEquals("/rates", log.endpoint());
        assertEquals("GET", log.httpMethod());
        assertSame(timestamp, log.timestamp());
    }

    @Test
    @DisplayName("constructor rejects null arguments")
    void constructorRejectsNulls() {
        Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");

        assertThrows(NullPointerException.class,
                () -> new RequestLog(null, "/rates", "GET", timestamp));
        assertThrows(NullPointerException.class,
                () -> new RequestLog("id-1", null, "GET", timestamp));
        assertThrows(NullPointerException.class,
                () -> new RequestLog("id-1", "/rates", null, timestamp));
        assertThrows(NullPointerException.class,
                () -> new RequestLog("id-1", "/rates", "GET", null));
    }
}
