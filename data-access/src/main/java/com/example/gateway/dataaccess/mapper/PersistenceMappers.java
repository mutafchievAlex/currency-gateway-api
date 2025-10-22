package com.example.gateway.dataaccess.mapper;

import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import com.example.gateway.dataaccess.entity.RequestLogEntity;
import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PersistenceMappers {

    @Mapping(target = "recordedAt", source = "rate", qualifiedByName = "exchangeRateTimestamp")
    ExchangeRateEntity toEntity(ExchangeRate rate);

    @Mapping(target = "timestamp", source = "entity", qualifiedByName = "exchangeRateEntityRecordedAt")
    ExchangeRate toDomain(ExchangeRateEntity entity);

    RequestLogEntity toEntity(RequestLog log);

    RequestLog toDomain(RequestLogEntity entity);

    @Mapping(target = "metric", source = "metricName")
    @Mapping(target = "recordedAt", source = "entry", qualifiedByName = "statisticsEntryRecordedAt")
    StatisticsEntryEntity toEntity(StatisticsEntry entry);

    @Mapping(target = "metricName", source = "metric")
    @Mapping(target = "recordedAt", source = "entity", qualifiedByName = "statisticsEntryEntityRecordedAt")
    StatisticsEntry toDomain(StatisticsEntryEntity entity);

    @Named("exchangeRateTimestamp")
    default Instant extractTimestamp(ExchangeRate rate) {
        return rate == null ? null : rate.timestamp();
    }

    @Named("exchangeRateEntityRecordedAt")
    default Instant extractRecordedAt(ExchangeRateEntity entity) {
        return entity == null ? null : entity.getRecordedAt();
    }

    @Named("statisticsEntryRecordedAt")
    default Instant extractStatisticsRecordedAt(StatisticsEntry entry) {
        return entry == null ? null : entry.recordedAt();
    }

    @Named("statisticsEntryEntityRecordedAt")
    default Instant extractEntityRecordedAt(StatisticsEntryEntity entity) {
        return entity == null ? null : entity.getRecordedAt();
    }
}
