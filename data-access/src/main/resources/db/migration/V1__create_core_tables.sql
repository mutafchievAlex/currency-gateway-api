-- Core relational schema for the currency gateway service

CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    base_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate NUMERIC(19, 6) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_exchange_rates_pair_timestamp UNIQUE (base_currency, target_currency, recorded_at)
);

CREATE INDEX idx_exchange_rates_recorded_at ON exchange_rates (recorded_at);
CREATE INDEX idx_exchange_rates_currencies ON exchange_rates (base_currency, target_currency);

CREATE TABLE request_logs (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    logged_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_request_logs_request_id UNIQUE (request_id)
);

CREATE INDEX idx_request_logs_timestamp ON request_logs (logged_at);
CREATE INDEX idx_request_logs_endpoint ON request_logs (endpoint);

CREATE TABLE statistics_entries (
    id BIGSERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value NUMERIC(19, 6) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_statistics_entries_metric_timestamp UNIQUE (metric_name, timestamp)
);

CREATE INDEX idx_statistics_entries_metric ON statistics_entries (metric_name);
CREATE INDEX idx_statistics_entries_recorded_at ON statistics_entries (timestamp);
