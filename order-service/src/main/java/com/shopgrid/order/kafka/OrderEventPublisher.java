package com.shopgrid.order.kafka;

import com.shopgrid.order.event.NotificationEvent;
import com.shopgrid.order.event.OrderCancelledEvent;
import com.shopgrid.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, NotificationEvent> notificationEventKafkaTemplate;
    private static final String TOPIC_NAME = "notification.requests.v1";

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order.created", event.orderId().toString(), event);
    }

    public void publishSendNotification(NotificationEvent event) {

        String key = event.userId().toString();

        CompletableFuture<SendResult<String, NotificationEvent>> future =
                notificationEventKafkaTemplate.send(TOPIC_NAME, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Notification sent successfully! Partition: {}, Offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed EventId: {}. Причина: {}",
                        event.eventId(), ex.getMessage());
            }
        });
    }

    public void publishCancelOrder(OrderCancelledEvent event) {
        log.info("Event published, cancel order event: {}", event.orderId());
        kafkaTemplate.send("order.cancelled", event.orderId().toString(), event);
    }
}
