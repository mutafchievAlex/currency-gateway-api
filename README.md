# Currency Gateway

Currency Gateway is a multi-module Spring Boot application that exposes unified currency exchange services. The
application fetches FX rates from external providers, stores them in PostgreSQL, and exposes JSON and XML APIs while
publishing statistics to RabbitMQ.

## Project layout

The project follows Clean Architecture principles and is organized in Maven modules:

- `domain` — core domain model and invariants
- `application` — use cases orchestrating business logic
- `common` — shared utilities such as exceptions and mappers
- `infrastructure` — implementations for persistence, messaging, and external clients
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

## Next steps

This repository currently contains the foundational structure (Phase 0). Subsequent phases will add the domain model,
persistence layer, external integrations, APIs, and comprehensive testing.
