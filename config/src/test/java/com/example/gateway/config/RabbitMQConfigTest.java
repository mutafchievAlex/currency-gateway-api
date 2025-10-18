package com.example.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMQConfigTest {

    private RabbitProperties rabbitProperties;
    private RabbitMQConfig config;

    @BeforeEach
    void setUp() {
        rabbitProperties = new RabbitProperties();
        rabbitProperties.setHost("rabbit.example.com");
        rabbitProperties.setPort(5673);
        rabbitProperties.setUsername("user");
        rabbitProperties.setPassword("secret");
        rabbitProperties.setVirtualHost("/gateway");

        config = new RabbitMQConfig(rabbitProperties, "currency.statistics");
    }

    @Test
    void createsCachingConnectionFactoryWithRabbitProperties() {
        ConnectionFactory connectionFactory = config.rabbitConnectionFactory();

        assertThat(connectionFactory).isInstanceOf(CachingConnectionFactory.class);
        CachingConnectionFactory cachingConnectionFactory = (CachingConnectionFactory) connectionFactory;

        assertThat(cachingConnectionFactory.getHost()).isEqualTo("rabbit.example.com");
        assertThat(cachingConnectionFactory.getPort()).isEqualTo(5673);
        assertThat(cachingConnectionFactory.getUsername()).isEqualTo("user");
        assertThat(cachingConnectionFactory.getVirtualHost()).isEqualTo("/gateway");
    }

    @Test
    void configuresRabbitTemplateWithExchangeAndMessageConverter() {
        ConnectionFactory connectionFactory = config.rabbitConnectionFactory();
        MessageConverter messageConverter = config.rabbitMessageConverter(new ObjectMapper());

        RabbitTemplate rabbitTemplate = config.rabbitTemplate(connectionFactory, messageConverter);

        assertThat(rabbitTemplate.getExchange()).isEqualTo("currency.statistics");
        assertThat(rabbitTemplate.getMessageConverter()).isSameAs(messageConverter);
    }

    @Test
    void createsDurableTopicExchange() {
        TopicExchange exchange = config.statisticsExchange();

        assertThat(exchange.getName()).isEqualTo("currency.statistics");
        assertThat(exchange.isDurable()).isTrue();
        assertThat(exchange.isAutoDelete()).isFalse();
    }
}
