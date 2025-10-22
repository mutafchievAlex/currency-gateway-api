package com.example.gateway.dataaccess.repository;

import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<StatisticsEntryEntity, Long> {

    List<StatisticsEntryEntity> findByMetricNameAndTimestampBetweenOrderByTimestampAsc(String metricName,
                                                                                       Instant start,
                                                                                       Instant end);
}
