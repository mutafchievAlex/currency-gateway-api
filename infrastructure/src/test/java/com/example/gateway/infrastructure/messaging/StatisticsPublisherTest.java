package com.example.gateway.infrastructure.messaging;

import com.example.gateway.domain.StatisticsEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticsPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private TopicExchange exchange;
    private StatisticsPublisher publisher;

    @BeforeEach
    void setUp() {
        exchange = new TopicExchange("currency.statistics");
        publisher = new StatisticsPublisher(rabbitTemplate, exchange);
    }

    @Test
    void publishesStatisticsEventWithDerivedRoutingKey() {
        StatisticsEntry entry = new StatisticsEntry(
                "request.count",
                new BigDecimal("12.34"),
                Instant.parse("2024-04-01T10:15:30Z"));

        publisher.publish(entry);

        ArgumentCaptor<StatisticsPublisher.StatisticsEvent> captor =
                ArgumentCaptor.forClass(StatisticsPublisher.StatisticsEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq(exchange.getName()),
                eq("statistics.request.count"),
                captor.capture());

        StatisticsPublisher.StatisticsEvent event = captor.getValue();
        assertThat(event.metric()).isEqualTo("request.count");
        assertThat(event.value()).isEqualTo(new BigDecimal("12.34"));
        assertThat(event.recordedAt()).isEqualTo(Instant.parse("2024-04-01T10:15:30Z"));
    }

    @Test
    void rejectsNullStatisticsEntry() {
        assertThrows(NullPointerException.class, () -> publisher.publish(null));
    }
}
