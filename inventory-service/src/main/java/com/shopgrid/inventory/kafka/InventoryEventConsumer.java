package com.shopgrid.inventory.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.inventory.event.OrderCancelledEvent;
import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.event.ProductCreatedEvent;
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
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.created", groupId = "inventory-service")
    public void handleOrderCreated(String message) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
            log.info("Received order created event: {}", event.orderId());
            service.reserveStock(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize OrderCreatedEvent: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "product.created", groupId = "inventory-service")
    public void handleProductCreated(String message) {
        try {
            String json = objectMapper.readValue(message, String.class);
            ProductCreatedEvent event = objectMapper.readValue(json, ProductCreatedEvent.class);
            log.info("Received product created event: {}", event.productId());
            service.createInventory(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize ProductCreatedEvent: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "order.cancelled", groupId = "inventory-service")
    public void handleOrderCancelled(String message) {
        try {
            OrderCancelledEvent event = objectMapper.readValue(message, OrderCancelledEvent.class);
            log.info("Received order cancelled event: {}", event.orderId());
            service.releaseStock(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize OrderCancelledEvent: {}", e.getMessage());
        }
    }

}
