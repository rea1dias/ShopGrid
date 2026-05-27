package com.shopgrid.inventory.service;

import com.shopgrid.inventory.event.OrderCreatedEvent;
import com.shopgrid.inventory.event.ProductCreatedEvent;

public interface InventoryService {

    void reserveStock(OrderCreatedEvent event);
    void createInventory(ProductCreatedEvent event);
}
