package com.example.gateway.dataaccess.persistence;

import com.example.gateway.domain.RequestLog;
import com.example.gateway.dataaccess.entity.RequestLogEntity;
import com.example.gateway.dataaccess.mapper.PersistenceMappers;
import com.example.gateway.dataaccess.repository.RequestLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class RequestLogRepositoryTest {

    private static final PersistenceMappers MAPPER = Mappers.getMapper(PersistenceMappers.class);

    @Autowired
    private RequestLogRepository repository;

    @Test
    void savesAndFindsByRequestId() {
        RequestLog log = new RequestLog("req-123", "/rates", "GET", Instant.now().truncatedTo(ChronoUnit.MILLIS));
        repository.saveAndFlush(MAPPER.toEntity(log));

        RequestLogEntity persisted = repository.findByRequestId("req-123").orElseThrow();

        assertThat(persisted.getEndpoint()).isEqualTo("/rates");
        assertThat(persisted.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void enforcesUniqueRequestId() {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        RequestLog log = new RequestLog("req-duplicate", "/metrics", "POST", timestamp);
        repository.saveAndFlush(MAPPER.toEntity(log));

        RequestLogEntity duplicate = MAPPER.toEntity(log);

        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
