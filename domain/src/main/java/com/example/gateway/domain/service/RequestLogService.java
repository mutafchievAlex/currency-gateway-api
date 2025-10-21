package com.example.gateway.domain.service;

import com.example.gateway.domain.model.RequestLog;

/**
 * Service boundary for persisting request logs.
 */
public interface RequestLogService {

    RequestLog record(RequestLog log);
}
