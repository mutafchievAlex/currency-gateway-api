package com.example.gateway.infrastructure.persistence.mapper;

import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.entity.RequestLogEntity;
import com.example.gateway.infrastructure.persistence.entity.StatisticsEntryEntity;

public final class PersistenceMappers {

    private PersistenceMappers() {
    }

    public static ExchangeRateEntity toEntity(ExchangeRate rate) {
        if (rate == null) {
            return null;
        }
        return new ExchangeRateEntity(
                rate.baseCurrency(),
                rate.targetCurrency(),
                rate.rate(),
                rate.timestamp()
        );
    }

    public static ExchangeRate toDomain(ExchangeRateEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ExchangeRate(
                entity.getBaseCurrency(),
                entity.getTargetCurrency(),
                entity.getRate(),
                entity.getRecordedAt()
        );
    }

    public static RequestLogEntity toEntity(RequestLog log) {
        if (log == null) {
            return null;
        }
        return new RequestLogEntity(
                log.requestId(),
                log.endpoint(),
                log.httpMethod(),
                log.timestamp()
        );
    }

    public static RequestLog toDomain(RequestLogEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RequestLog(
                entity.getRequestId(),
                entity.getEndpoint(),
                entity.getHttpMethod(),
                entity.getLoggedAt()
        );
    }

    public static StatisticsEntryEntity toEntity(StatisticsEntry entry) {
        if (entry == null) {
            return null;
        }
        return new StatisticsEntryEntity(
                entry.metricName(),
                entry.value(),
                entry.recordedAt()
        );
    }

    public static StatisticsEntry toDomain(StatisticsEntryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new StatisticsEntry(
                entity.getMetricName(),
                entity.getValue(),
                entity.getRecordedAt()
        );
    }
}
