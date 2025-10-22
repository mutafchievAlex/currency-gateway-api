package com.example.gateway.dataaccess.mapper;

import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import com.example.gateway.dataaccess.entity.RequestLogEntity;
import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.model.RequestLog;
import com.example.gateway.domain.model.StatisticsEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersistenceMappers {

    ExchangeRateEntity toEntity(ExchangeRate rate);

    ExchangeRate toDomain(ExchangeRateEntity entity);

    RequestLogEntity toEntity(RequestLog log);

    RequestLog toDomain(RequestLogEntity entity);

    StatisticsEntryEntity toEntity(StatisticsEntry entry);

    StatisticsEntry toDomain(StatisticsEntryEntity entity);
}
