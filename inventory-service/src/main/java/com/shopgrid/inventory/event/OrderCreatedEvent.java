package com.shopgrid.inventory.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        List<OrderItemEvent> items,
        BigDecimal totalPrice,
        Instant createdAt
) {
}
