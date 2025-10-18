package com.example.gateway.infrastructure.persistence.repository;

import com.example.gateway.infrastructure.persistence.entity.StatisticsEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<StatisticsEntryEntity, Long> {

    List<StatisticsEntryEntity> findByMetricNameAndRecordedAtBetweenOrderByRecordedAtAsc(String metricName,
                                                                                         Instant start,
                                                                                         Instant end);
}
