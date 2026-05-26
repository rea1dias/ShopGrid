package com.shopgrid.inventory.service;

import com.shopgrid.inventory.event.OrderCreatedEvent;

public interface InventoryService {

    void reserveStock(OrderCreatedEvent event);
}
