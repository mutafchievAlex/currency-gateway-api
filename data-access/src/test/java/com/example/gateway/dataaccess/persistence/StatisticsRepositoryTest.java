package com.example.gateway.dataaccess.persistence;

import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.dataaccess.entity.StatisticsEntryEntity;
import com.example.gateway.dataaccess.mapper.PersistenceMappers;
import com.example.gateway.dataaccess.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StatisticsRepositoryTest {

    private static final PersistenceMappers MAPPER = Mappers.getMapper(PersistenceMappers.class);

    @Autowired
    private StatisticsRepository repository;

    @Test
    void findsEntriesWithinInterval() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        repository.save(MAPPER.toEntity(new StatisticsEntry("requests", new BigDecimal("1.0"), now.minusSeconds(120))));
        repository.save(MAPPER.toEntity(new StatisticsEntry("requests", new BigDecimal("2.0"), now.minusSeconds(60))));
        repository.save(MAPPER.toEntity(new StatisticsEntry("requests", new BigDecimal("3.0"), now)));
        repository.save(MAPPER.toEntity(new StatisticsEntry("errors", new BigDecimal("1.0"), now)));

        List<StatisticsEntryEntity> entries = repository
                .findByMetricNameAndRecordedAtBetweenOrderByRecordedAtAsc("requests", now.minusSeconds(90), now.plusSeconds(1));

        assertThat(entries).hasSize(2);
        assertThat(entries.get(0).getValue()).isEqualByComparingTo("2.0");
        assertThat(entries.get(1).getValue()).isEqualByComparingTo("3.0");
    }
}
