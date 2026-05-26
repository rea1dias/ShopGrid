package com.shopgrid.inventory.event;

import java.util.UUID;

public record StockFailedEvent(
        UUID orderId,
        UUID userId,
        String reason
) {}
