package com.example.gateway.api.xml;

import com.example.gateway.api.support.ExchangeRatesApiMapper;
import com.example.gateway.api.xml.generated.model.ApiError;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateResponse;
import com.example.gateway.domain.ExchangeRate;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;

import java.util.List;

@Mapper(componentModel = "spring")
public interface XmlApiMapper extends ExchangeRatesApiMapper<ExchangeRateResponse, ExchangeRateHistoryResponse, ApiError> {

    @Override
    ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate);

    @Override
    List<ExchangeRateResponse> toExchangeRateResponses(List<ExchangeRate> rates);

    @Override
    default ExchangeRateHistoryResponse toHistoryResponse(List<ExchangeRate> rates) {
        ExchangeRateHistoryResponse response = new ExchangeRateHistoryResponse();
        response.setRates(toExchangeRateResponses(rates));
        return response;
    }

    @Override
    default ApiError createError(HttpStatus status, String title, String detail) {
        ApiError error = new ApiError();
        error.setTitle(title);
        error.setDetail(safeDetail(detail, title));
        error.setStatus(status.value());
        return error;
    }
}
