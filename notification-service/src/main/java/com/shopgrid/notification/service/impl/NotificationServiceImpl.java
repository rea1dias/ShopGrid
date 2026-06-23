package com.shopgrid.notification.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
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
    private final Resend resend;

    @Override
    @Transactional
    public void sendNotification(NotificationEvent event) {
        if (repository.existsByEventId(event.eventId())) {
            log.warn("Duplicate event ignored: {}", event.eventId());
            return;
        }
        log.info("Event received: {}", event.orderId());
        Notification notification = new Notification(event.eventId(), event.userId().toString(), event.channel(), event.recipient(), null, NotificationStatus.PENDING, null, Instant.now(), Instant.now());
        try {
            repository.save(notification);
            switch (event.channel()) {
                case PUSH ->
                        log.info("[PUSH stub] userId={}, orderId={}, message=Ваш заказ #{} подтверждён", event.userId(), event.orderId(), event.orderId());
                case SMS ->
                        log.info("[SMS stub] recipient={}, orderId={}, message=Ваш заказ #{} подтверждён", event.recipient(), event.orderId(), event.orderId());
                case EMAIL -> sendEmail(event);
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

    private void sendEmail(NotificationEvent event) {
        try {
            String subject = switch (event.template()) {
                case ORDER_CONFIRMED -> "Ваш заказ #" + event.orderId() + " подтверждён";
                case ORDER_CANCELLED -> "Ваш заказ #" + event.orderId() + " отменён";
                default -> "Уведомление о заказе #" + event.orderId();
            };

            String html = switch (event.template()) {
                case ORDER_CONFIRMED -> """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background-color: #4F46E5; padding: 20px; border-radius: 8px 8px 0 0;">
                                <h1 style="color: white; margin: 0;">ShopGrid</h1>
                            </div>
                            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px;">
                                <h2 style="color: #333;">Ваш заказ подтверждён! 🎉</h2>
                                <p style="color: #666;">Спасибо за покупку в ShopGrid.</p>
                                <div style="background-color: white; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px; margin: 20px 0;">
                                    <p style="margin: 0; color: #999; font-size: 14px;">Номер заказа</p>
                                    <p style="margin: 5px 0; font-size: 18px; font-weight: bold; color: #333;">#%s</p>
                                    <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 15px 0;">
                                    <p style="margin: 0; color: #999; font-size: 14px;">Сумма заказа</p>
                                    <p style="margin: 5px 0; font-size: 24px; font-weight: bold; color: #4F46E5;">%s ₸</p>
                                </div>
                                <p style="color: #666; font-size: 14px;">Мы уведомим вас когда заказ будет отправлен.</p>
                                <p style="color: #999; font-size: 12px; margin-top: 30px;">© 2026 ShopGrid. Все права защищены.</p>
                            </div>
                        </div>
                        """.formatted(event.orderId(), event.totalPrice());

                case ORDER_CANCELLED -> """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                            <div style="background-color: #EF4444; padding: 20px; border-radius: 8px 8px 0 0;">
                                <h1 style="color: white; margin: 0;">ShopGrid</h1>
                            </div>
                            <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px;">
                                <h2 style="color: #333;">Ваш заказ отменён 😔</h2>
                                <p style="color: #666;">К сожалению, ваш заказ был отменён.</p>
                                <div style="background-color: white; border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px; margin: 20px 0;">
                                    <p style="margin: 0; color: #999; font-size: 14px;">Номер заказа</p>
                                    <p style="margin: 5px 0; font-size: 18px; font-weight: bold; color: #333;">#%s</p>
                                    <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 15px 0;">
                                    <p style="margin: 0; color: #999; font-size: 14px;">Сумма заказа</p>
                                    <p style="margin: 5px 0; font-size: 24px; font-weight: bold; color: #EF4444;">%s ₸</p>
                                </div>
                                <p style="color: #666; font-size: 14px;">Если у вас есть вопросы — свяжитесь с поддержкой.</p>
                                <p style="color: #999; font-size: 12px; margin-top: 30px;">© 2026 ShopGrid. Все права защищены.</p>
                            </div>
                        </div>
                        """.formatted(event.orderId(), event.totalPrice());

                default -> "<p>Уведомление о заказе #%s</p>".formatted(event.orderId());
            };

            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(event.recipient())
                    .subject(subject)
                    .html(html)
                    .build();

            CreateEmailResponse response = resend.emails().send(request);
            log.info("Email sent via Resend, id: {}", response.getId());

        } catch (ResendException e) {
            log.error("Failed to send email via Resend: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
