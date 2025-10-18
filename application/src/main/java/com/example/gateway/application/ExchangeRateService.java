package com.example.gateway.application;

import com.example.gateway.application.port.ExchangeRateRepositoryPort;
import com.example.gateway.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Application service that encapsulates persistence rules for {@link ExchangeRate} instances.
 */
@Service
public class ExchangeRateService {

    private final ExchangeRateRepositoryPort repository;

    public ExchangeRateService(ExchangeRateRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    public boolean saveIfAbsent(ExchangeRate rate) {
        Objects.requireNonNull(rate, "rate must not be null");
        return repository.findByPairAndTimestamp(rate.baseCurrency(), rate.targetCurrency(), rate.timestamp())
                .map(existing -> false)
                .orElseGet(() -> {
                    repository.save(rate);
                    return true;
                });
    }
}
