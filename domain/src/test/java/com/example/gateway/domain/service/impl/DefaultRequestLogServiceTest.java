package com.example.gateway.domain.service.impl;

import com.example.gateway.common.exception.DuplicateRequestException;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.repository.RequestLogRepositoryPort;
import com.example.gateway.domain.validation.BeanValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultRequestLogServiceTest {

    @Mock
    private RequestLogRepositoryPort repository;

    @Mock
    private BeanValidationService validationService;

    @InjectMocks
    private DefaultRequestLogService service;

    private RequestLog log;

    @BeforeEach
    void setUp() {
        log = new RequestLog("req-1", "/rates", "GET", Instant.parse("2024-03-15T10:15:30Z"));
        when(validationService.requireValid(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void recordsNewRequestWhenMissing() {
        when(repository.findByRequestId(log.requestId())).thenReturn(Optional.empty());
        when(repository.save(log)).thenReturn(log);

        RequestLog recorded = service.record(log);

        assertEquals(log, recorded);
        verify(repository).save(log);
    }

    @Test
    void throwsExceptionWhenDuplicateRequestDetected() {
        when(repository.findByRequestId(log.requestId())).thenReturn(Optional.of(log));

        assertThrows(DuplicateRequestException.class, () -> service.record(log));
        verify(repository, never()).save(log);
    }
}
