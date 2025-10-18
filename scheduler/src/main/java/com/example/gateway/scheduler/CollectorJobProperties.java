package com.example.gateway.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Configuration holder for the periodic rate collection job.
 */
@Component
@ConfigurationProperties(prefix = "collector")
public class CollectorJobProperties {

    private String baseCurrency = "USD";
    private List<String> targetCurrencies = new ArrayList<>();

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        if (!StringUtils.hasText(baseCurrency)) {
            throw new IllegalArgumentException("collector.base-currency must not be blank");
        }
        this.baseCurrency = baseCurrency.trim();
    }

    public List<String> getTargetCurrencies() {
        return Collections.unmodifiableList(targetCurrencies);
    }

    public void setTargetCurrencies(List<String> targetCurrencies) {
        if (targetCurrencies == null) {
            this.targetCurrencies = new ArrayList<>();
            return;
        }

        this.targetCurrencies = targetCurrencies.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
