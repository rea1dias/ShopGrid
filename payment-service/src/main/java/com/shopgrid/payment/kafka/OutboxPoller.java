package com.shopgrid.payment.kafka;

import com.shopgrid.payment.event.OutboxEvent;
import com.shopgrid.payment.repo.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class OutboxPoller {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;

    public OutboxPoller(OutboxEventRepository outboxEventRepository, @Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void poll() {
        List<OutboxEvent> events = outboxEventRepository.findTop100BySentFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(topicFor(event.getEventType()), event.getPayload()).get();
                event.markSent();
                log.info("Outbox event {} sent to Kafka", event.getId());
            } catch (Exception e) {
                log.error("Failed to send outbox event {}: {}", event.getId(), e.getMessage());
            }
        }
    }

    private String topicFor(String eventType) {
        return switch (eventType) {
            case "payment.completed" -> "payment.completed";
            case "payment.failed" -> "payment.failed";
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}
