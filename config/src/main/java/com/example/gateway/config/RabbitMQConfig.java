package com.example.gateway.config;

import com.example.gateway.common.validation.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures RabbitMQ components used by the application.
 */
@Configuration
public class RabbitMQConfig {

    private final RabbitProperties rabbitProperties;
    private final String exchangeName;

    public RabbitMQConfig(RabbitProperties rabbitProperties,
                          @Value("${spring.rabbitmq.template.exchange}") String exchangeName) {
        this.rabbitProperties = rabbitProperties;
        this.exchangeName = ValidationUtils.requireTrimmedNotBlank(exchangeName, "exchangeName");
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());

        String virtualHost = rabbitProperties.getVirtualHost();
        if (virtualHost != null && !virtualHost.isBlank()) {
            connectionFactory.setVirtualHost(virtualHost);
        }

        return connectionFactory;
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter rabbitMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(exchangeName);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange statisticsExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
}
