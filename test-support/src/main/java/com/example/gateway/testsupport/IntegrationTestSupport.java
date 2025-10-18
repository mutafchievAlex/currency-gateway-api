package com.example.gateway.testsupport;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class that bootstraps shared Testcontainers instances for integration tests.
 */
@Testcontainers(disabledWithoutDocker = true)
public abstract class IntegrationTestSupport {

    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String RABBITMQ_IMAGE = "rabbitmq:3.13.0";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("currency_gateway")
            .withUsername("currency")
            .withPassword("currency");

    @Container
    private static final RabbitMQContainer RABBITMQ = new RabbitMQContainer(RABBITMQ_IMAGE);

    @DynamicPropertySource
    protected static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("spring.rabbitmq.host", RABBITMQ::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBITMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBITMQ::getAdminPassword);
        registry.add("spring.rabbitmq.template.exchange", () -> "statistics.integration");

        registry.add("spring.task.scheduling.enabled", () -> "false");
        registry.add("fixer.api-key", () -> "test-access-key");
        registry.add("fixer.url", () -> "http://localhost/api");
    }
}
