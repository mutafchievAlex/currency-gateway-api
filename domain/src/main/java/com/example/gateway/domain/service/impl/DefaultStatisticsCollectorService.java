package com.example.gateway.domain.service.impl;

import com.example.gateway.domain.validation.ValidationUtils;
import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.domain.repository.StatisticsRepositoryPort;
import com.example.gateway.domain.service.StatisticsCollectorService;
import com.example.gateway.domain.validation.BeanValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DefaultStatisticsCollectorService implements StatisticsCollectorService {

    private final StatisticsRepositoryPort repository;
    private final BeanValidationService validationService;

    @Autowired
    public DefaultStatisticsCollectorService(StatisticsRepositoryPort repository, BeanValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @Override
    public StatisticsEntry record(StatisticsEntry entry) {
        StatisticsEntry candidate = validationService.requireValid(entry, "entry");
        return repository.save(candidate);
    }

    @Override
    public List<StatisticsEntry> retrieve(String metricName, Instant start, Instant end) {
        String safeMetric = ValidationUtils.requireTrimmedNotBlank(metricName, "metricName");
        Instant safeStart = validationService.requirePresent(start, "start");
        Instant safeEnd = validationService.requirePresent(end, "end");
        return repository.findEntriesWithin(safeMetric, safeStart, safeEnd);
    }
}
