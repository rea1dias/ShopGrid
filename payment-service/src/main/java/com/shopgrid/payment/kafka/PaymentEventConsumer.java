package com.shopgrid.payment.kafka;

import com.shopgrid.payment.event.StockReservedEvent;
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

    @KafkaListener(topics = "stock.reserved", groupId = "payment-service",
            properties = "spring.json.value.default.type=com.shopgrid.payment.event.StockReservedEvent")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("Received order created event: {}", event.orderId());
        service.processPayment(event);
    }

}
