package com.shopgrid.order.kafka;

import com.shopgrid.order.common.enums.OrderStatus;
import com.shopgrid.order.event.PaymentCompletedEvent;
import com.shopgrid.order.event.PaymentFailedEvent;
import com.shopgrid.order.event.StockFailedEvent;
import com.shopgrid.order.event.StockReservedEvent;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderService service;

    @KafkaListener(topics = "stock.reserved", groupId = "order-service",
            properties = {"spring.json.value.default.type=com.shopgrid.order.event.StockReservedEvent"})
    public void handleStockReserved(StockReservedEvent event) {
        log.info("Received stock reserved event: {}", event.orderId());
        service.update(event.orderId(), OrderStatus.RESERVED);
    }

    @KafkaListener(topics = "stock.failed", groupId = "order-service",
            properties = {"spring.json.value.default.type=com.shopgrid.order.event.StockFailedEvent"})
    public void handleStockFailed(StockFailedEvent event) {
        log.info("Received stock failed event: {}", event.orderId());
        service.update(event.orderId(), OrderStatus.CANCELLED);
    }

    @KafkaListener(topics = "payment.completed", groupId = "order-service",
            properties = {"spring.json.value.default.type=com.shopgrid.order.event.PaymentCompletedEvent"})
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment completed event: {}", event.orderId());
        service.update(event.orderId(), OrderStatus.CONFIRMED);
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service",
            properties = {"spring.json.value.default.type=com.shopgrid.order.event.PaymentFailedEvent"})
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Received payment failed event: {}", event.orderId());
        service.update(event.orderId(), OrderStatus.PAYMENT_FAILED);
    }
}
