package com.shopgrid.inventory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.inventory.domain.dto.request.InventoryUpdateRequest;
import com.shopgrid.inventory.domain.dto.response.InventoryResponse;
import com.shopgrid.inventory.domain.entity.Inventory;
import com.shopgrid.inventory.event.*;
import com.shopgrid.inventory.exception.InsufficientStockException;
import com.shopgrid.inventory.exception.NotFoundException;
import com.shopgrid.inventory.mapper.InventoryMapper;
import com.shopgrid.inventory.repo.InventoryRepository;
import com.shopgrid.inventory.repo.OutboxEventRepository;
import com.shopgrid.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper mapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
        try {
            for (OrderItemEvent item : event.items()) {
                Inventory inventory = inventoryRepository.findByProductId(item.productId()).orElseThrow(() -> new NotFoundException(item.productId()));
                if (inventory.getQuantity() < item.quantity()) {
                    throw new InsufficientStockException(item.productId());
                }
                inventory.setQuantity(inventory.getQuantity() - item.quantity());
                inventory.setReserved(inventory.getReserved() + item.quantity());
                inventoryRepository.save(inventory);
            }
            try {
                String payload = objectMapper.writeValueAsString(event);
                outboxEventRepository.save(new OutboxEvent("stock.reserved", payload));
                log.info("Reserved stock: {}", event.orderId());
            } catch (Exception e) {
                log.error("Failed to reserve stock.reserved for orderId: {}, error: {}", event.orderId(), e.getMessage());
            }
        } catch (Exception e) {
            try {
                String payload = objectMapper.writeValueAsString(event);
                outboxEventRepository.save(new OutboxEvent("stock.failed", payload));
            } catch (Exception ex) {
                log.error("Failed to persist stock.failed event for orderId: {}", event.orderId(), ex);
            }
        }
    }

    @Override
    public void releaseStock(OrderCancelledEvent event) {
        try {
            for (OrderItemEvent item : event.items()) {
                Inventory inventory = inventoryRepository.findByProductId(item.productId()).orElseThrow(() -> new NotFoundException(item.productId()));
                inventory.setQuantity(inventory.getQuantity() + item.quantity());
                inventory.setReserved(inventory.getReserved() - item.quantity());
                inventoryRepository.save(inventory);
                log.info("Released stock: {}", inventory.getReserved());
            }
        } catch (Exception e) {
            log.error("Failed to release stock for orderId: {}, error: {}", event.orderId(), e.getMessage());
        }
    }

    @Override
    public void createInventory(ProductCreatedEvent event) {
        if (inventoryRepository.findByProductId(event.productId()).isPresent()) {
            log.info("Inventory already exists: {}", event.productId());
            return;
        }
        Inventory inventory = new Inventory(event.productId(), 0, 0);
        inventoryRepository.save(inventory);
    }

    @Override
    public void refundStock(RefundApprovedEvent event) {
        try {
            for (RefundItemEvent item : event.items()) {
                Inventory inventory = inventoryRepository.findByProductId(item.productId())
                        .orElseThrow(() -> new NotFoundException(item.productId()));
                inventory.setQuantity(inventory.getQuantity() + item.quantity());
                inventoryRepository.save(inventory);
            }
            String payload = objectMapper.writeValueAsString(
                    new RefundInventoryCompletedEvent(event.refundId(), event.orderId())
            );
            outboxEventRepository.save(new OutboxEvent("refund.inventory.completed", payload));
            log.info("Refunded stock for refundId: {}", event.refundId());
        } catch (Exception e) {
            log.error("Failed to refund stock for refundId: {}, error: {}", event.refundId(), e.getMessage());
        }

    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponse add(UUID productId, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new NotFoundException(productId));
        inventory.setQuantity(inventory.getQuantity() + request.quantity());
        return mapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse get(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new NotFoundException(productId));
        return mapper.toResponse(inventory);
    }
}
