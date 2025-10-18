package com.example.gateway.infrastructure.persistence;

import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.entity.RequestLogEntity;
import com.example.gateway.infrastructure.persistence.entity.StatisticsEntryEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceMappersTest {

    @Test
    void roundTripsExchangeRate() {
        ExchangeRate domain = new ExchangeRate("USD", "EUR", new BigDecimal("0.9"), Instant.parse("2024-01-01T00:00:00Z"));

        ExchangeRateEntity entity = PersistenceMappers.toEntity(domain);
        ExchangeRate mappedBack = PersistenceMappers.toDomain(entity);

        assertThat(mappedBack).isEqualTo(domain);
    }

    @Test
    void roundTripsRequestLog() {
        RequestLog domain = new RequestLog("id-1", "/endpoint", "GET", Instant.parse("2024-01-01T00:01:00Z"));

        RequestLogEntity entity = PersistenceMappers.toEntity(domain);
        RequestLog mappedBack = PersistenceMappers.toDomain(entity);

        assertThat(mappedBack).isEqualTo(domain);
    }

    @Test
    void roundTripsStatisticsEntry() {
        StatisticsEntry domain = new StatisticsEntry("metric", new BigDecimal("1.23"), Instant.parse("2024-01-01T00:02:00Z"));

        StatisticsEntryEntity entity = PersistenceMappers.toEntity(domain);
        StatisticsEntry mappedBack = PersistenceMappers.toDomain(entity);

        assertThat(mappedBack).isEqualTo(domain);
    }
}
