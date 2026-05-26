package com.shopgrid.inventory.event;

import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        UUID userId
) {}
