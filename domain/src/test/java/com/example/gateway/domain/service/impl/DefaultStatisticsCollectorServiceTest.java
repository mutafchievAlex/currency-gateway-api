package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.domain.repository.StatisticsRepositoryPort;
import com.example.gateway.domain.validation.BeanValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultStatisticsCollectorServiceTest {

    @Mock
    private StatisticsRepositoryPort repository;

    @Mock
    private BeanValidationService validationService;

    @InjectMocks
    private DefaultStatisticsCollectorService service;

    private StatisticsEntry entry;

    @BeforeEach
    void setUp() {
        entry = new StatisticsEntry("requests", new BigDecimal("42"), Instant.parse("2024-03-15T10:15:30Z"));
        when(validationService.requireValid(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(validationService.requirePresent(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void persistsStatisticsEntry() {
        when(repository.save(entry)).thenReturn(entry);

        StatisticsEntry persisted = service.record(entry);

        assertEquals(entry, persisted);
        verify(repository).save(entry);
    }

    @Test
    void retrievesEntriesWithinInterval() {
        Instant start = Instant.parse("2024-03-15T10:00:00Z");
        Instant end = Instant.parse("2024-03-15T11:00:00Z");
        List<StatisticsEntry> expected = List.of(entry);
        when(repository.findEntriesWithin("requests", start, end)).thenReturn(expected);

        List<StatisticsEntry> actual = service.retrieve("requests", start, end);

        assertEquals(expected, actual);
        verify(repository).findEntriesWithin("requests", start, end);
    }
}
