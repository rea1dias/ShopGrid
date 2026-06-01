package com.shopgrid.notification.kafka;

import com.shopgrid.notification.event.NotificationEvent;
import com.shopgrid.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService service;

    @KafkaListener(topics = "notification.requests.v1", groupId = "notification-service",
            properties = {"spring.json.value.default.type=com.shopgrid.notification.event.NotificationEvent"})
    public void handleNotificationEvent(NotificationEvent event, Acknowledgment ack) {
        log.info("Received notification event: {}", event.orderId());
        service.sendNotification(event);
        ack.acknowledge();
    }

}
