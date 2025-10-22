package com.example.gateway.dataaccess.repository;

import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    Optional<ExchangeRateEntity> findFirstByBaseCurrencyAndTargetCurrencyOrderByTimestampDesc(String baseCurrency,
                                                                                              String targetCurrency);

    Optional<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrencyAndTimestamp(String baseCurrency,
                                                                                  String targetCurrency,
                                                                                  Instant timestamp);

    List<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrencyAndTimestampBetweenOrderByTimestampAsc(String baseCurrency,
                                                                                                       String targetCurrency,
                                                                                                       Instant start,
                                                                                                       Instant end);
}
