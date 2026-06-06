package com.shopgrid.notification.service.impl;

import com.shopgrid.notification.common.entity.Notification;
import com.shopgrid.notification.common.enums.NotificationStatus;
import com.shopgrid.notification.event.NotificationEvent;
import com.shopgrid.notification.repo.NotificationRepository;
import com.shopgrid.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    @Transactional
    public void sendNotification(NotificationEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event ignored: {}", event.eventId());
            return;
        }
        log.info("Event received: {}", event.orderId());
        Notification notification = new Notification(
                event.eventId(),
                event.userId().toString(),
                event.channel(),
                event.recipient(),
                null,
                NotificationStatus.PENDING,
                null,
                Instant.now(),
                Instant.now()
        );
        try {
            repository.save(notification);
             switch (event.channel()) {
                case PUSH -> log.info("[PUSH stub] userId={}, orderId={}, message=Ваш заказ #{} подтверждён",
                        event.userId(), event.orderId(), event.orderId());
                case SMS -> log.info("[SMS stub] recipient={}, orderId={}, message=Ваш заказ #{} подтверждён",
                        event.recipient(), event.orderId(), event.orderId());
                case EMAIL -> log.info("[EMAIL stub] recipient={}, orderId={}",
                        event.recipient(), event.orderId());
            }

            notification.setStatus(NotificationStatus.SENT);
            notification.setUpdatedAt(Instant.now());
            repository.save(notification);

            log.info("Notification sent id: {}", notification.getId());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.warn("Duplicate event ignored (concurrent): {}", event.eventId());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setUpdatedAt(Instant.now());
            repository.save(notification);
            log.error("Failed to send notification: {}", e.getMessage());
        }

    }
}
