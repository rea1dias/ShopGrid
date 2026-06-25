package com.shopgrid.payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.payment.event.PaymentRequestedEvent;
import com.shopgrid.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment.requested", groupId = "payment-service")
    public void handlePaymentRequested(String message) {
        try {
            PaymentRequestedEvent event = objectMapper.readValue(message, PaymentRequestedEvent.class);
            log.info("Received payment requested event: {}", event.orderId());
            service.processPayment(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize PaymentRequestedEvent: {}", e.getMessage());
        }
    }
}
