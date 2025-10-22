package com.example.gateway.scheduler.messaging;

import com.example.gateway.domain.exception.MissingRequiredValueException;
import com.example.gateway.domain.validation.ValidationUtils;
import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.domain.validation.BeanValidationService;
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

    public record StatisticsEvent(String metricName, BigDecimal value, Instant timestamp) {

        private StatisticsEvent {
            metricName = ValidationUtils.requireTrimmedNotBlank(metricName, "metricName");
            if (value == null) {
                throw MissingRequiredValueException.forField("value");
            }
            if (timestamp == null) {
                throw MissingRequiredValueException.forField("timestamp");
            }
        }

        public static StatisticsEvent from(StatisticsEntry entry) {
            return new StatisticsEvent(entry.metricName(), entry.value(), entry.timestamp());
        }
    }
}
