package com.example.gateway.dataaccess.persistence;

import com.example.gateway.domain.model.RequestLog;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class RequestLogRepositoryTest {

    private static final PersistenceMappers MAPPER = Mappers.getMapper(PersistenceMappers.class);

    @Autowired
    private RequestLogRepository repository;

    @Test
    void savesAndFindsByRequestId() {
        UUID requestId = UUID.fromString("77777777-7777-7777-7777-777777777777");
        RequestLog log = new RequestLog(requestId, "/rates", "GET", Instant.now().truncatedTo(ChronoUnit.MILLIS));
        repository.saveAndFlush(MAPPER.toEntity(log));

        RequestLogEntity persisted = repository.findByRequestId(requestId).orElseThrow();

        assertThat(persisted.getEndpoint()).isEqualTo("/rates");
        assertThat(persisted.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void enforcesUniqueRequestId() {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        RequestLog log = new RequestLog(UUID.fromString("88888888-8888-8888-8888-888888888888"), "/metrics", "POST", timestamp);
        repository.saveAndFlush(MAPPER.toEntity(log));

        RequestLogEntity duplicate = MAPPER.toEntity(log);

        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
