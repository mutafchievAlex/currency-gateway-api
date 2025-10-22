package com.example.gateway.dataaccess.mapper;

import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import com.example.gateway.dataaccess.entity.RequestLogEntity;
import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersistenceMappers {

    @Mapping(target = "recordedAt", expression = "java(rate.timestamp())")
    ExchangeRateEntity toEntity(ExchangeRate rate);

    @Mapping(target = "timestamp", expression = "java(entity.getRecordedAt())")
    ExchangeRate toDomain(ExchangeRateEntity entity);

    RequestLogEntity toEntity(RequestLog log);

    RequestLog toDomain(RequestLogEntity entity);

    @Mapping(target = "metric", expression = "java(entry.metricName())")
    StatisticsEntryEntity toEntity(StatisticsEntry entry);

    @Mapping(target = "metricName", expression = "java(entity.getMetric())")
    StatisticsEntry toDomain(StatisticsEntryEntity entity);
}
