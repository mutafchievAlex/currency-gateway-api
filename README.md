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
- `api-json` — REST controllers and DTOs for JSON consumers
- `api-xml` — XML controllers and DTOs
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
