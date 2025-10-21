FROM postgres:16-alpine

ENV POSTGRES_DB=currency_gateway \
    POSTGRES_USER=currency \
    POSTGRES_PASSWORD=currency

COPY infrastructure/src/main/resources/db/migration /docker-entrypoint-initdb.d/
