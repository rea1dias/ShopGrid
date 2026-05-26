package com.shopgrid.inventory.kafka;

import com.shopgrid.inventory.event.StockFailedEvent;
import com.shopgrid.inventory.event.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStockReserved(StockReservedEvent event) {
        log.info("Publishing stock reserved event: {}", event.orderId());
        kafkaTemplate.send("stock.reserved", event.orderId().toString(), event);
    }

    public void publishStockFailed(StockFailedEvent event) {
        log.info("Publishing stock failed: {}", event.orderId());
        kafkaTemplate.send("stock.failed", event.orderId().toString(), event);
    }
}
