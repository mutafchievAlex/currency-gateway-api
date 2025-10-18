package com.example.gateway.infrastructure.messaging;

import com.example.gateway.domain.StatisticsEntry;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Publishes statistics events to RabbitMQ.
 */
@Component
public class StatisticsPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange statisticsExchange;

    public StatisticsPublisher(RabbitTemplate rabbitTemplate, TopicExchange statisticsExchange) {
        this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate, "rabbitTemplate must not be null");
        this.statisticsExchange = Objects.requireNonNull(statisticsExchange, "statisticsExchange must not be null");
    }

    public void publish(StatisticsEntry entry) {
        Objects.requireNonNull(entry, "entry must not be null");

        String routingKey = buildRoutingKey(entry.metricName());
        rabbitTemplate.convertAndSend(statisticsExchange.getName(), routingKey, StatisticsEvent.from(entry));
    }

    private String buildRoutingKey(String metricName) {
        return "statistics." + metricName;
    }

    public record StatisticsEvent(String metric, BigDecimal value, Instant recordedAt) {

        private StatisticsEvent {
            Objects.requireNonNull(metric, "metric must not be null");
            Objects.requireNonNull(value, "value must not be null");
            Objects.requireNonNull(recordedAt, "recordedAt must not be null");
        }

        public static StatisticsEvent from(StatisticsEntry entry) {
            return new StatisticsEvent(entry.metricName(), entry.value(), entry.recordedAt());
        }
    }
}
