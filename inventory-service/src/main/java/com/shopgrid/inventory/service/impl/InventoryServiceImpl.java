package com.shopgrid.inventory.service.impl;

import com.shopgrid.inventory.domain.dto.request.InventoryUpdateRequest;
import com.shopgrid.inventory.domain.dto.response.InventoryResponse;
import com.shopgrid.inventory.domain.entity.Inventory;
import com.shopgrid.inventory.event.*;
import com.shopgrid.inventory.exception.InsufficientStockException;
import com.shopgrid.inventory.exception.NotFoundException;
import com.shopgrid.inventory.kafka.InventoryEventPublisher;
import com.shopgrid.inventory.mapper.InventoryMapper;
import com.shopgrid.inventory.repo.InventoryRepository;
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
    private final InventoryEventPublisher publisher;
    private final InventoryMapper mapper;

    @Override
    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
        try {
            for (OrderItemEvent item : event.items()) {
                Inventory inventory = inventoryRepository.findByProductId(item.productId())
                        .orElseThrow(() -> new NotFoundException(item.productId()));
                if (inventory.getQuantity() < item.quantity()) {
                    throw new InsufficientStockException(item.productId());
                }
                inventory.setQuantity(inventory.getQuantity() - item.quantity());
                log.info("Reserving stock for productId: {}", item.productId());
                inventory.setReserved(inventory.getReserved() + item.quantity());
                inventoryRepository.save(inventory);
                log.info("Reserved stock for productId: {}", item.productId());
            }
            publisher.publishStockReserved(new StockReservedEvent(event.orderId(), event.userId()));
        } catch (Exception e) {
            publisher.publishStockFailed(new StockFailedEvent(event.orderId(), event.userId(), e.getMessage()));
        }
    }

    @Override
    public void createInventory(ProductCreatedEvent event) {
        if (inventoryRepository.findById(event.productId()).isPresent()) {
            log.info("Inventory already exists: {}", event.productId());
            return;
        }
        Inventory inventory = new Inventory(
                event.productId(),
                0,
                0
        );
        inventoryRepository.save(inventory);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public InventoryResponse add(UUID productId, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException(productId));
        inventory.setQuantity(inventory.getQuantity() + request.quantity());
        return mapper.toResponse(inventoryRepository.save(inventory));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse get(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException(productId));
        return mapper.toResponse(inventory);
    }
}
