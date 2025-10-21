package com.example.gateway.application;

import com.example.gateway.application.port.StatisticsRepositoryPort;
import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.StatisticsEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StatisticsCollectorService {

    private final StatisticsRepositoryPort repository;
    private final BeanValidationService validationService;

    public StatisticsCollectorService(StatisticsRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    public StatisticsEntry record(StatisticsEntry entry) {
        StatisticsEntry candidate = validationService.requireValid(entry, "entry");
        return repository.save(candidate);
    }

    public List<StatisticsEntry> retrieve(String metricName, Instant start, Instant end) {
        String safeMetric = ValidationUtils.requireTrimmedNotBlank(metricName, "metricName");
        Instant safeStart = validationService.requirePresent(start, "start");
        Instant safeEnd = validationService.requirePresent(end, "end");
        return repository.findEntriesWithin(safeMetric, safeStart, safeEnd);
    }
}
