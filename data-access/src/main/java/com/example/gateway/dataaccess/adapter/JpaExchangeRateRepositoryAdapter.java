package com.example.gateway.dataaccess.adapter;

import com.example.gateway.domain.model.ExchangeRate;
import com.example.gateway.domain.repository.ExchangeRateRepositoryPort;
import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import com.example.gateway.dataaccess.mapper.PersistenceMappers;
import com.example.gateway.dataaccess.repository.ExchangeRateRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class JpaExchangeRateRepositoryAdapter implements ExchangeRateRepositoryPort {

    private final ExchangeRateRepository repository;
    private final PersistenceMappers mapper;

    public JpaExchangeRateRepositoryAdapter(ExchangeRateRepository repository, PersistenceMappers mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ExchangeRate> findByPairAndTimestamp(String baseCurrency, String targetCurrency, Instant timestamp) {
        return repository.findByBaseCurrencyAndTargetCurrencyAndTimestamp(baseCurrency, targetCurrency, timestamp)
                .map(mapper::toDomain);
    }

    @Override
    public ExchangeRate save(ExchangeRate rate) {
        ExchangeRateEntity saved = repository.save(mapper.toEntity(rate));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ExchangeRate> findLatestByPair(String baseCurrency, String targetCurrency) {
        return repository.findFirstByBaseCurrencyAndTargetCurrencyOrderByTimestampDesc(baseCurrency, targetCurrency)
                .map(mapper::toDomain);
    }

    @Override
    public List<ExchangeRate> findWithinRange(String baseCurrency, String targetCurrency, Instant start, Instant end) {
        return repository
                .findByBaseCurrencyAndTargetCurrencyAndTimestampBetweenOrderByTimestampAsc(baseCurrency, targetCurrency, start, end)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
