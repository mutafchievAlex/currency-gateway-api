package com.example.gateway.infrastructure.persistence;

import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import com.example.gateway.infrastructure.persistence.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ExchangeRateRepositoryTest {

    @Autowired
    private ExchangeRateRepository repository;

    @Test
    void saveAndFindLatestByCurrencyPair() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        repository.save(PersistenceMappers.toEntity(new ExchangeRate("USD", "EUR", new BigDecimal("0.9000"), now.minusSeconds(60))));
        repository.save(PersistenceMappers.toEntity(new ExchangeRate("USD", "EUR", new BigDecimal("0.9100"), now)));
        repository.save(PersistenceMappers.toEntity(new ExchangeRate("USD", "GBP", new BigDecimal("0.8000"), now)));

        ExchangeRateEntity latest = repository
                .findFirstByBaseCurrencyAndTargetCurrencyOrderByRecordedAtDesc("USD", "EUR")
                .orElseThrow();

        assertThat(latest.getRate()).isEqualByComparingTo("0.9100");
        assertThat(latest.getRecordedAt()).isEqualTo(now);
    }

    @Test
    void preventsDuplicateRecordsForSameTimestamp() {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ExchangeRate rate = new ExchangeRate("USD", "JPY", new BigDecimal("110.0000"), timestamp);
        repository.saveAndFlush(PersistenceMappers.toEntity(rate));

        ExchangeRateEntity duplicate = PersistenceMappers.toEntity(rate);

        assertThatThrownBy(() -> repository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
