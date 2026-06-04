package com.shopgrid.inventory.service.impl;

import com.shopgrid.inventory.domain.entity.Inventory;
import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.event.OrderItemEvent;
import com.shopgrid.inventory.event.StockFailedEvent;
import com.shopgrid.inventory.event.StockReservedEvent;
import com.shopgrid.inventory.kafka.InventoryEventPublisher;
import com.shopgrid.inventory.repo.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceImplTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryEventPublisher inventoryEventPublisher;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    public void reserveInventorySuccessfully() {
        OrderItemEvent itemEvent = new OrderItemEvent(
                UUID.randomUUID(),
                "productName",
                BigDecimal.valueOf(15999.00),
                5
        );
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(itemEvent),
                BigDecimal.valueOf(36000.00),
                Instant.now());

        Inventory inventory = new Inventory(
                itemEvent.productId(),
                20,
                10);
        when(inventoryRepository.findByProductId(itemEvent.productId())).thenReturn(Optional.of(inventory));
        inventoryService.reserveStock(event);
        verify(inventoryRepository).save(inventory);
        verify(inventoryEventPublisher).publishStockReserved(any(StockReservedEvent.class));
    }

    @Test
    public void notEnoughGoods() {
        OrderItemEvent itemEvent = new OrderItemEvent(
                UUID.randomUUID(),
                "productName",
                BigDecimal.valueOf(15999.00),
                90
        );
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(itemEvent),
                BigDecimal.valueOf(36000.00),
                Instant.now());

        Inventory inventory = new Inventory(
                itemEvent.productId(),
                30,
                10);
        when(inventoryRepository.findByProductId(itemEvent.productId())).thenReturn(Optional.of(inventory));
        inventoryService.reserveStock(event);
        verify(inventoryEventPublisher).publishStockFailed(any(StockFailedEvent.class));
    }

    @Test
    public void inventoryNotFound() {
        OrderItemEvent itemEvent = new OrderItemEvent(
                UUID.randomUUID(),
                "productName",
                BigDecimal.valueOf(15999.00),
                90
        );
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(itemEvent),
                BigDecimal.valueOf(36000.00),
                Instant.now());
        when(inventoryRepository.findByProductId(itemEvent.productId()))
                .thenReturn(Optional.empty());

        inventoryService.reserveStock(event);
        verify(inventoryEventPublisher).publishStockFailed(any(StockFailedEvent.class));
        verify(inventoryEventPublisher, never()).publishStockReserved(any());
    }

    @Test
    public void fallback() {
        OrderItemEvent itemEvent = new OrderItemEvent(
                UUID.randomUUID(),
                "productName",
                BigDecimal.valueOf(15999.00),
                100
        );
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                List.of(itemEvent),
                BigDecimal.valueOf(36000.00),
                Instant.now());
        Inventory inventory = new Inventory(
                itemEvent.productId(),
                900,
                10);

        when(inventoryRepository.findByProductId(itemEvent.productId()))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        inventoryService.reserveStock(event);
        verify(inventoryEventPublisher).publishStockFailed(any(StockFailedEvent.class));
        verify(inventoryEventPublisher, never()).publishStockReserved(any());
    }
}
