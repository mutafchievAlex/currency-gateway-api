package com.example.gateway.config;

import com.example.gateway.CurrencyGatewayApplication;
import com.example.gateway.api.json.generated.model.ApiErrorResponse;
import com.example.gateway.api.json.generated.model.ExchangeRateResponse;
import com.example.gateway.api.xml.generated.model.ExchangeRateHistoryResponse;
import com.example.gateway.domain.model.StatisticsEntry;
import com.example.gateway.domain.service.StatisticsCollectorService;
import com.example.gateway.scheduler.messaging.StatisticsPublisher;
import com.example.gateway.dataaccess.entity.ExchangeRateEntity;
import com.example.gateway.dataaccess.repository.ExchangeRateRepository;
import com.example.gateway.dataaccess.repository.RequestLogRepository;
import com.example.gateway.dataaccess.repository.StatisticsRepository;
import com.example.gateway.testsupport.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CurrencyGatewayApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private TopicExchange statisticsExchange;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MessageConverter messageConverter;

    @Autowired
    private StatisticsCollectorService statisticsCollectorService;

    @Autowired
    private StatisticsPublisher statisticsPublisher;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void cleanDatabase() {
        requestLogRepository.deleteAll();
        statisticsRepository.deleteAll();
        exchangeRateRepository.deleteAll();
    }

    @Test
    void shouldReturnLatestRateThroughJsonEndpoint() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        exchangeRateRepository.save(new ExchangeRateEntity(1L,"USD", "EUR", new BigDecimal("0.90"), now.minusSeconds(60)));
        exchangeRateRepository.save(new ExchangeRateEntity(2L,"USD", "EUR", new BigDecimal("0.95"), now));

        String uri = UriComponentsBuilder.fromPath("/api/exchange-rates/current")
                .queryParam("requestId", "json-req-1")
                .queryParam("baseCurrency", "USD")
                .queryParam("targetCurrency", "EUR")
                .toUriString();

        ResponseEntity<ExchangeRateResponse> response = restTemplate.getForEntity(uri, ExchangeRateResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBaseCurrency()).isEqualTo("USD");
        assertThat(response.getBody().getTargetCurrency()).isEqualTo("EUR");
        assertThat(response.getBody().getRate()).isEqualTo("0.95");
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp().toInstant()).isEqualTo(now);
        assertThat(requestLogRepository.findByRequestId("json-req-1")).isPresent();
    }

    @Test
    void shouldReturnHistoryThroughXmlEndpoint() throws Exception {
        Instant first = Instant.now().minusSeconds(120).truncatedTo(ChronoUnit.MILLIS);
        Instant second = first.plusSeconds(30);
        Instant third = first.plusSeconds(60);

        exchangeRateRepository.save(new ExchangeRateEntity(3L,"USD", "EUR", new BigDecimal("0.91"), first));
        exchangeRateRepository.save(new ExchangeRateEntity(4L,"USD", "EUR", new BigDecimal("0.92"), second));
        exchangeRateRepository.save(new ExchangeRateEntity(5L,"USD", "EUR", new BigDecimal("0.93"), third));

        String uri = UriComponentsBuilder.fromPath("/api/exchange-rates/history")
                .queryParam("requestId", "xml-req-1")
                .queryParam("baseCurrency", "usd")
                .queryParam("targetCurrency", "eur")
                .queryParam("start", first.minusSeconds(5))
                .queryParam("end", third.plusSeconds(5))
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_XML));

        ResponseEntity<ExchangeRateHistoryResponse> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ExchangeRateHistoryResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requestLogRepository.findByRequestId("xml-req-1")).isPresent();

        ExchangeRateHistoryResponse historyResponse = response.getBody();
        assertThat(historyResponse).isNotNull();
        assertThat(historyResponse.getRates()).hasSize(3);
        assertThat(historyResponse.getRates().get(0).getRate()).isEqualTo("0.91");
        assertThat(historyResponse.getRates().get(1).getRate()).isEqualTo("0.92");
        assertThat(historyResponse.getRates().get(2).getRate()).isEqualTo("0.93");
    }

    @Test
    void shouldRejectDuplicateRequestIdsAcrossEndpoints() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        exchangeRateRepository.save(new ExchangeRateEntity(1L,"USD", "GBP", new BigDecimal("0.80"), now));

        String uri = UriComponentsBuilder.fromPath("/api/exchange-rates/current")
                .queryParam("requestId", "dup-req-1")
                .queryParam("baseCurrency", "USD")
                .queryParam("targetCurrency", "GBP")
                .toUriString();

        ResponseEntity<ExchangeRateResponse> firstResponse = restTemplate.getForEntity(uri, ExchangeRateResponse.class);
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<ApiErrorResponse> duplicateResponse = restTemplate.getForEntity(uri, ApiErrorResponse.class);
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(duplicateResponse.getBody()).isNotNull();
        assertThat(duplicateResponse.getBody().getMessage()).contains("dup-req-1");
    }

    @Test
    void shouldPersistStatisticsEntries() {
        Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        StatisticsEntry entry = new StatisticsEntry("request.count", new BigDecimal("42.50"), timestamp);

        statisticsCollectorService.record(entry);

        List<StatisticsEntry> entries = statisticsCollectorService.retrieve(
                "request.count",
                timestamp.minusSeconds(5),
                timestamp.plusSeconds(5)
        );

        assertThat(entries)
                .singleElement()
                .satisfies(stored -> {
                    assertThat(stored.getMetricName()).isEqualTo("request.count");
                    assertThat(stored.getValue()).isEqualByComparingTo("42.50");
                    assertThat(stored.getTimestamp()).isEqualTo(timestamp);
                });
    }

    @Test
    void shouldPublishStatisticsEventsToRabbitMq() {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        String queueName = "statistics.integration." + UUID.randomUUID();
        Queue queue = QueueBuilder.nonDurable(queueName)
                .autoDelete()
                .exclusive()
                .build();
        admin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue)
                .to(statisticsExchange)
                .with("statistics.request.total");
        admin.declareBinding(binding);

        StatisticsEntry entry = new StatisticsEntry(
                "request.total",
                new BigDecimal("12.34"),
                Instant.now().truncatedTo(ChronoUnit.MILLIS)
        );

        statisticsPublisher.publish(entry);

        RabbitTemplate receivingTemplate = new RabbitTemplate(connectionFactory);
        receivingTemplate.setMessageConverter(messageConverter);
        receivingTemplate.setReceiveTimeout(5000);

        Object received = receivingTemplate.receiveAndConvert(queue.getName());

        assertThat(received).isInstanceOf(StatisticsPublisher.StatisticsEvent.class);
        StatisticsPublisher.StatisticsEvent event = (StatisticsPublisher.StatisticsEvent) received;
        assertThat(event.metricName()).isEqualTo("request.total");
        assertThat(event.value()).isEqualByComparingTo("12.34");
    }
}
