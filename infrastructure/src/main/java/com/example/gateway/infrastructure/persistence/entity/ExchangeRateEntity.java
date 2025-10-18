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

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Column(name = "target_currency", nullable = false, length = 3)
    private String targetCurrency;

    @Column(name = "rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal rate;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected ExchangeRateEntity() {
        // JPA
    }

    public ExchangeRateEntity(String baseCurrency, String targetCurrency, BigDecimal rate, Instant recordedAt) {
        this.baseCurrency = Objects.requireNonNull(baseCurrency, "baseCurrency");
        this.targetCurrency = Objects.requireNonNull(targetCurrency, "targetCurrency");
        this.rate = Objects.requireNonNull(rate, "rate");
        this.recordedAt = Objects.requireNonNull(recordedAt, "recordedAt");
    }

    public Long getId() {
        return id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
