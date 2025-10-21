package com.example.gateway.infrastructure.messaging;

import com.example.gateway.application.validation.BeanValidationService;
import com.example.gateway.common.exception.MissingRequiredValueException;
import com.example.gateway.common.validation.ValidationUtils;
import com.example.gateway.domain.StatisticsEntry;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Publishes statistics events to RabbitMQ.
 */
@Component
public class StatisticsPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange statisticsExchange;
    private final BeanValidationService validationService;

    public StatisticsPublisher(RabbitTemplate rabbitTemplate,
                               TopicExchange statisticsExchange,
                               BeanValidationService validationService) {
        this.validationService = validationService;
        this.rabbitTemplate = validationService.requirePresent(rabbitTemplate, "rabbitTemplate");
        this.statisticsExchange = validationService.requirePresent(statisticsExchange, "statisticsExchange");
    }

    public void publish(StatisticsEntry entry) {
        StatisticsEntry candidate = validationService.requireValid(entry, "entry");

        String routingKey = buildRoutingKey(candidate.metricName());
        rabbitTemplate.convertAndSend(statisticsExchange.getName(), routingKey, StatisticsEvent.from(candidate));
    }

    private String buildRoutingKey(String metricName) {
        return "statistics." + metricName;
    }

    public record StatisticsEvent(String metric, BigDecimal value, Instant recordedAt) {

        private StatisticsEvent {
            metric = ValidationUtils.requireTrimmedNotBlank(metric, "metric");
            if (value == null) {
                throw MissingRequiredValueException.forField("value");
            }
            if (recordedAt == null) {
                throw MissingRequiredValueException.forField("recordedAt");
            }
        }

        public static StatisticsEvent from(StatisticsEntry entry) {
            return new StatisticsEvent(entry.metricName(), entry.value(), entry.recordedAt());
        }
    }
}
