package com.example.gateway.dataaccess.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "exchange_rates",
        indexes = {
                @Index(name = "idx_exchange_rates_recorded_at", columnList = "recorded_at"),
                @Index(name = "idx_exchange_rates_currencies", columnList = "base_currency,target_currency")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_exchange_rates_pair_timestamp",
                        columnNames = {"base_currency", "target_currency", "recorded_at"})
        })
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @NotBlank
    @Column(name = "target_currency", nullable = false, length = 3)
    private String targetCurrency;

    @NotNull
    @Column(name = "rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

}
