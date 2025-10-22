package com.example.gateway.dataaccess.entity;

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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
@Entity
@Table(name = "statistics_entries",
        indexes = {
                @Index(name = "idx_statistics_metric", columnList = "metric"),
                @Index(name = "idx_statistics_timestamp", columnList = "recorded_at")
        })
public class StatisticsEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "metric", nullable = false)
    private String metricName;

    @NotNull
    @Column(name = "value", nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private Instant timestamp;

}
