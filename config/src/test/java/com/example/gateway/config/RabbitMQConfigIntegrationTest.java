package com.example.gateway.config;

import com.example.gateway.domain.StatisticsEntry;
import com.example.gateway.infrastructure.messaging.StatisticsPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {RabbitMQConfig.class, RabbitMQConfigIntegrationTest.TestConfig.class},
        properties = "spring.rabbitmq.template.exchange=statistics.integration"
)
@Testcontainers(disabledWithoutDocker = true)
class RabbitMQConfigIntegrationTest {

    @Container
    static final RabbitMQContainer rabbitMqContainer = new RabbitMQContainer("rabbitmq:3.13.0");

    @DynamicPropertySource
    static void registerRabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMqContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMqContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMqContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMqContainer::getAdminPassword);
    }

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;
    private final ConnectionFactory connectionFactory;
    private final MessageConverter messageConverter;

    RabbitMQConfigIntegrationTest(RabbitTemplate rabbitTemplate,
                                  TopicExchange topicExchange,
                                  ConnectionFactory connectionFactory,
                                  MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.topicExchange = topicExchange;
        this.connectionFactory = connectionFactory;
        this.messageConverter = messageConverter;
    }

    @Test
    void shouldConnectToRabbitMqAndPublishMessage() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        Queue queue = QueueBuilder.nonDurable("statistics.integration.queue")
                .autoDelete()
                .exclusive()
                .build();
        admin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(topicExchange)
                .with("statistics.test");
        admin.declareBinding(binding);

        StatisticsPublisher.StatisticsEvent event = StatisticsPublisher.StatisticsEvent.from(
                new StatisticsEntry("test", BigDecimal.ONE, Instant.now())
        );
        rabbitTemplate.convertAndSend(topicExchange.getName(), "statistics.test", event);

        RabbitTemplate receivingTemplate = new RabbitTemplate(connectionFactory);
        receivingTemplate.setMessageConverter(messageConverter);
        receivingTemplate.setReceiveTimeout(5000);

        Object received = receivingTemplate.receiveAndConvert(queue.getName());

        assertThat(received)
                .as("A message should be received from RabbitMQ")
                .isInstanceOf(StatisticsPublisher.StatisticsEvent.class)
                .isEqualTo(event);
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
