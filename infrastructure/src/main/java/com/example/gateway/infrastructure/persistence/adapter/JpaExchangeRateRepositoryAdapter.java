package com.example.gateway.infrastructure.persistence.adapter;

import com.example.gateway.application.port.ExchangeRateRepositoryPort;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import com.example.gateway.infrastructure.persistence.mapper.PersistenceMappers;
import com.example.gateway.infrastructure.persistence.repository.ExchangeRateRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class JpaExchangeRateRepositoryAdapter implements ExchangeRateRepositoryPort {

    private final ExchangeRateRepository repository;

    public JpaExchangeRateRepositoryAdapter(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ExchangeRate> findByPairAndTimestamp(String baseCurrency, String targetCurrency, Instant timestamp) {
        return repository.findByBaseCurrencyAndTargetCurrencyAndRecordedAt(baseCurrency, targetCurrency, timestamp)
                .map(PersistenceMappers::toDomain);
    }

    @Override
    public ExchangeRate save(ExchangeRate rate) {
        ExchangeRateEntity saved = repository.save(PersistenceMappers.toEntity(rate));
        return PersistenceMappers.toDomain(saved);
    }

    @Override
    public Optional<ExchangeRate> findLatestByPair(String baseCurrency, String targetCurrency) {
        return repository.findFirstByBaseCurrencyAndTargetCurrencyOrderByRecordedAtDesc(baseCurrency, targetCurrency)
                .map(PersistenceMappers::toDomain);
    }

    @Override
    public List<ExchangeRate> findWithinRange(String baseCurrency, String targetCurrency, Instant start, Instant end) {
        return repository
                .findByBaseCurrencyAndTargetCurrencyAndRecordedAtBetweenOrderByRecordedAtAsc(baseCurrency, targetCurrency, start, end)
                .stream()
                .map(PersistenceMappers::toDomain)
                .toList();
    }
}
