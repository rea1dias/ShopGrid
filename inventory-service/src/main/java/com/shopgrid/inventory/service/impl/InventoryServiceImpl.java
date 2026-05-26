package com.shopgrid.inventory.service.impl;

import com.shopgrid.inventory.domain.entity.Inventory;
import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.event.OrderItemEvent;
import com.shopgrid.inventory.exception.InsufficientStockException;
import com.shopgrid.inventory.exception.NotFoundException;
import com.shopgrid.inventory.repo.InventoryRepository;
import com.shopgrid.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void reserveStock(OrderCreatedEvent event) {
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
    }
}
