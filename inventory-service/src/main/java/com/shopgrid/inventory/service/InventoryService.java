package com.shopgrid.inventory.service;

import com.shopgrid.inventory.domain.dto.request.InventoryUpdateRequest;
import com.shopgrid.inventory.domain.dto.response.InventoryResponse;
import com.shopgrid.inventory.event.OrderCancelledEvent;
import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.event.ProductCreatedEvent;

import java.util.UUID;

public interface InventoryService {

    void reserveStock(OrderCreatedEvent event);

    void releaseStock(OrderCancelledEvent event);

    void createInventory(ProductCreatedEvent event);

    InventoryResponse add(UUID productId, InventoryUpdateRequest request);

    InventoryResponse get(UUID productId);
}
