package com.example.gateway.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "statistics_entries",
        indexes = {
                @Index(name = "idx_statistics_entries_metric", columnList = "metric_name"),
                @Index(name = "idx_statistics_entries_recorded_at", columnList = "recorded_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_statistics_entries_metric_timestamp",
                        columnNames = {"metric_name", "recorded_at"})
        })
public class StatisticsEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected StatisticsEntryEntity() {
        // JPA
    }

    public StatisticsEntryEntity(String metricName, BigDecimal value, Instant recordedAt) {
        this.metricName = Objects.requireNonNull(metricName, "metricName");
        this.value = Objects.requireNonNull(value, "value");
        this.recordedAt = Objects.requireNonNull(recordedAt, "recordedAt");
    }

    public Long getId() {
        return id;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
