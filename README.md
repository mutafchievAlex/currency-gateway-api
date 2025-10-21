# Currency Gateway

Currency Gateway is a multi-module Spring Boot application that exposes unified currency exchange services. The
application fetches FX rates from external providers, stores them in PostgreSQL, and exposes JSON and XML APIs while
publishing statistics to RabbitMQ.

## Project layout

The project follows Clean Architecture principles and is organized in Maven modules:

- `domain` — core domain model and invariants
- `application` — use cases orchestrating business logic
- `common` — shared utilities such as exceptions and mappers
- `data-access` — persistence adapters, entities, and mappers backed by JPA
- `scheduler` — background jobs responsible for periodic tasks
- `api-controller` — unified REST controllers that negotiate JSON and XML payloads
- `config` — Spring Boot bootstrap module and runtime configuration
- `test-support` — reusable test fixtures and utilities

## Getting started

### Prerequisites

- Java 17+
- Maven 3.9+

### Build

```bash
mvn clean package
```

To skip tests during the initial bootstrap phase:

```bash
mvn -DskipTests package
```

### Run

#### Start RabbitMQ locally

The application expects a RabbitMQ broker to be available. A Docker setup is
provided under `docker/` to spin up a dedicated instance pre-configured with
the topic exchange used by the gateway:

```bash
docker compose -f docker/docker-compose.rabbitmq.yml up --build
```

The broker exposes AMQP on `localhost:5672` and the management UI on
`http://localhost:15672` (default credentials `gateway` / `gateway`). You can
override credentials and the virtual host with the `RABBITMQ_USERNAME`,
`RABBITMQ_PASSWORD`, and `RABBITMQ_VHOST` environment variables when starting
the compose stack.

#### Start PostgreSQL locally

A Docker setup is provided to spin up a PostgreSQL instance for local
development. Flyway manages the schema when the Spring Boot application starts,
so the container itself does not pre-create any tables.

```bash
docker compose -f docker/docker-compose.postgres.yml up --build
```

The database is exposed on `localhost:5432` with the default credentials
`currency` / `currency` and the database name `currency_gateway`. You can change
them via the `DB_USERNAME`, `DB_PASSWORD`, and `DB_NAME` environment variables
when launching the compose stack.

The migrations live under `data-access/src/main/resources/db/migration`.
`V1__create_core_tables.sql` provisions the application tables and is applied by
Flyway on application startup so that local databases stay in sync with the
schema expected by the service.

The Spring Boot application entry point is located in the `config` module:

```bash
cd config
mvn spring-boot:run
```

### Configuration

Runtime configuration lives in `config/src/main/resources/application.yml`. Provide the following environment variables
when running locally or in production:

- `FIXER_API_KEY`
- `DB_USERNAME`, `DB_PASSWORD`
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- `RABBITMQ_EXCHANGE`

These values are resolved via standard Spring Boot configuration properties.

### Database schema

The schema is managed via Flyway migrations stored in
`data-access/src/main/resources/db/migration`. The initial migration creates
the following tables:

- `exchange_rates` — stores individual exchange rate observations together with
  their timestamp and currency pair. It enforces uniqueness across
  `(base_currency, target_currency, recorded_at)` and provides indexes for
  querying by timestamp and currency pair.
- `request_logs` — captures API requests with their identifier, endpoint, HTTP
  method, and timestamp. The `request_id` column is unique to prevent duplicate
  processing and indexes make lookup by timestamp or endpoint efficient.
- `statistics_entries` — contains metric samples collected by the gateway. Each
  row stores a metric name, its numeric value, and the moment it was recorded,
  with uniqueness on `(metric_name, recorded_at)` and supporting indexes for
  retrieval by metric or timestamp.

## Next steps

This repository currently contains the foundational structure (Phase 0). Subsequent phases will add the domain model,
persistence layer, external integrations, APIs, and comprehensive testing.
