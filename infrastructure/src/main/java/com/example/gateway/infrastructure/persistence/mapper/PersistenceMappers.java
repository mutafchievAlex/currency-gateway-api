package com.example.gateway.infrastructure.persistence.mapper;

import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;
import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.entity.RequestLogEntity;
import com.example.gateway.infrastructure.persistence.entity.StatisticsEntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersistenceMappers {

    @Mapping(target = "recordedAt", source = "timestamp")
    ExchangeRateEntity toEntity(ExchangeRate rate);

    @Mapping(target = "timestamp", source = "recordedAt")
    ExchangeRate toDomain(ExchangeRateEntity entity);

    RequestLogEntity toEntity(RequestLog log);

    RequestLog toDomain(RequestLogEntity entity);

    @Mapping(target = "metric", source = "metricName")
    StatisticsEntryEntity toEntity(StatisticsEntry entry);

    @Mapping(target = "metricName", source = "metric")
    StatisticsEntry toDomain(StatisticsEntryEntity entity);
}
