package com.shopgrid.order.kafka;

import com.shopgrid.order.common.enums.OrderStatus;
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

    @KafkaListener(topics = "stock.reserved", groupId = "order-service")
    public void handleStockReserved(StockReservedEvent event) {
        log.info("Received stock reserved event: {}", event.orderId());
        service.update(event.orderId(), OrderStatus.CONFIRMED);
    }

    @KafkaListener(topics = "stock.failed", groupId = "order-service")
    public void handleStockFailed(StockFailedEvent event) {
        log.info("Received stock failed event: {}", event);
        service.update(event.orderId(), OrderStatus.CANCELLED);
    }
}
