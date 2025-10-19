package com.example.gateway.api.xml;

import com.example.gateway.api.xml.generated.model.ExchangeRateCommandRequest;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.ExchangeRate;
import com.example.gateway.domain.RequestLog;

import java.time.Instant;
import java.util.List;

final class XmlApiMapper {

    private XmlApiMapper() {
    }

    static ExchangeRateResponse toResponse(ExchangeRate rate) {
        if (rate == null) {
            return null;
        }
        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setBaseCurrency(rate.baseCurrency());
        response.setTargetCurrency(rate.targetCurrency());
        response.setRate(rate.rate());
        response.setTimestamp(rate.timestamp());
        return response;
    }

    static ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        if (rates != null) {
            response.setRates(rates.stream()
                    .map(XmlApiMapper::toResponse)
                    .toList());
        }
        return response;
    }

    static RequestLog toRequestLog(ExchangeRateCommandRequest command, String endpoint, Instant timestamp) {
        return new RequestLog(normalize(command.getRequestId()), endpoint, "POST", timestamp);
    }

    static String normalizeCurrency(String currency) {
        if (currency == null) {
            return null;
        }
        return ValidationUtils.normalizeCurrencyCode(currency, "currency");
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
