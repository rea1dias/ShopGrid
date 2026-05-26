package com.shopgrid.inventory.kafka;

import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final InventoryService service;

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order created event: {}", event.orderId());
        service.reserveStock(event);
    }

}
