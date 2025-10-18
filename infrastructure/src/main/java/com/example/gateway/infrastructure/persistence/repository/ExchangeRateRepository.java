package com.example.gateway.infrastructure.persistence.repository;

import com.example.gateway.infrastructure.persistence.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findFirstByBaseCurrencyAndTargetCurrencyOrderByRecordedAtDesc(String baseCurrency,
                                                                                                String targetCurrency);

    Optional<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrencyAndRecordedAt(String baseCurrency,
                                                                                  String targetCurrency,
                                                                                  Instant recordedAt);

    List<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrencyAndRecordedAtBetweenOrderByRecordedAtAsc(String baseCurrency,
                                                                                                         String targetCurrency,
                                                                                                         Instant start,
                                                                                                         Instant end);
}
