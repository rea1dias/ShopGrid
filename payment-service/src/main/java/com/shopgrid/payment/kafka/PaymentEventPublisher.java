package com.shopgrid.payment.kafka;

import com.shopgrid.payment.event.PaymentCompletedEvent;
import com.shopgrid.payment.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void paymentSuccessEvent(PaymentCompletedEvent event) {
        kafkaTemplate.send("payment.completed", event.orderId().toString(), event);
    }

    public void paymentFailureEvent(PaymentFailedEvent event) {
        kafkaTemplate.send("payment.failed", event.orderId().toString(), event);
    }
}
