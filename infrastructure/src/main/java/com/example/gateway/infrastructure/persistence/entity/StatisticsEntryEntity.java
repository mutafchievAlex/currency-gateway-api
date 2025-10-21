package com.example.gateway.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "statistics_entries",
        indexes = {
                @Index(name = "idx_statistics_metric", columnList = "metric"),
                @Index(name = "idx_statistics_recorded_at", columnList = "recorded_at")
        })
public class StatisticsEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "metric", nullable = false)
    private String metric;

    @NotNull
    @Column(name = "value", nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected StatisticsEntryEntity() {
        // JPA
    }

    public StatisticsEntryEntity(String metric, BigDecimal value, Instant recordedAt) {
        this.metric = metric;
        this.value = value;
        this.recordedAt = recordedAt;
    }

    public Long getId() {
        return id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
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
