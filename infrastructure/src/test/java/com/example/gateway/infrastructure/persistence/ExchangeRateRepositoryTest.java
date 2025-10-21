package com.example.gateway.infrastructure.persistence;

import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import com.example.gateway.infrastructure.persistence.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ExchangeRateRepositoryTest {

    private static final PersistenceMappers MAPPER = Mappers.getMapper(PersistenceMappers.class);

    @Autowired
    private ExchangeRateRepository repository;

    @Test
    void saveAndFindLatestByCurrencyPair() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        repository.save(MAPPER.toEntity(new ExchangeRate("USD", "EUR", new BigDecimal("0.9000"), now.minusSeconds(60))));
        repository.save(MAPPER.toEntity(new ExchangeRate("USD", "EUR", new BigDecimal("0.9100"), now)));
        repository.save(MAPPER.toEntity(new ExchangeRate("USD", "GBP", new BigDecimal("0.8000"), now)));

        ExchangeRateEntity latest = repository
                .findFirstByBaseCurrencyAndTargetCurrencyOrderByRecordedAtDesc("USD", "EUR")
                .orElseThrow();

        assertThat(latest.getRate()).isEqualByComparingTo("0.9100");
        assertThat(latest.getRecordedAt()).isEqualTo(now);
    }

    @Test
    void findRatesWithinRangeOrderedAscending() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ExchangeRate older = new ExchangeRate("USD", "CAD", new BigDecimal("1.2500"), now.minusSeconds(120));
        ExchangeRate middle = new ExchangeRate("USD", "CAD", new BigDecimal("1.2600"), now.minusSeconds(60));
        ExchangeRate newer = new ExchangeRate("USD", "CAD", new BigDecimal("1.2700"), now);
        repository.save(MAPPER.toEntity(older));
        repository.save(MAPPER.toEntity(middle));
        repository.save(MAPPER.toEntity(newer));

        List<ExchangeRateEntity> results = repository
                .findByBaseCurrencyAndTargetCurrencyAndRecordedAtBetweenOrderByRecordedAtAsc(
                        "USD", "CAD", now.minusSeconds(180), now.plusSeconds(1));

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getRecordedAt()).isEqualTo(older.timestamp());
        assertThat(results.get(2).getRecordedAt()).isEqualTo(newer.timestamp());
    }

    @Test
    void preventsDuplicateRecordsForSameTimestamp() {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ExchangeRate rate = new ExchangeRate("USD", "JPY", new BigDecimal("110.0000"), timestamp);
        repository.saveAndFlush(MAPPER.toEntity(rate));

        ExchangeRateEntity duplicate = MAPPER.toEntity(rate);

        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
